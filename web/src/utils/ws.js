import { Message } from 'element-ui'

export default class WSClient {
  constructor(url) {
    this.url = url
    this.handlers = {}
    this.lastTsByType = {}
    this.heartbeatTimer = null
    this.retryTimer = null
    this.connect()
  }

  connect() {
    this.ws = new WebSocket(this.url)
    this.ws.onopen = () => {
      Message.success('实时连接已建立')
      this.startHeartbeat()
    }
    this.ws.onmessage = (evt) => {
      const msg = JSON.parse(evt.data || '{}')
      if (msg.type && this.lastTsByType[msg.type] && msg.ts <= this.lastTsByType[msg.type]) return
      if (msg.type) this.lastTsByType[msg.type] = msg.ts || Date.now()
      ;(this.handlers[msg.type] || []).forEach(fn => fn(msg))
    }
    this.ws.onclose = () => {
      clearInterval(this.heartbeatTimer)
      Message.warning('实时连接断开，正在重连...')
      this.retryTimer = setTimeout(() => this.connect(), 3000)
    }
  }

  startHeartbeat() {
    clearInterval(this.heartbeatTimer)
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === 1) this.ws.send('ping')
    }, 15000)
  }

  subscribe(type, cb) {
    if (!this.handlers[type]) this.handlers[type] = []
    this.handlers[type].push(cb)
  }
}
