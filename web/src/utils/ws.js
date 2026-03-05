export default class WSClient {
  constructor(url) {
    this.url = url
    this.handlers = {}
    this.heartbeatTimer = null
    this.retryTimer = null
    this.connect()
  }

  connect() {
    this.ws = new WebSocket(this.url)
    this.ws.onopen = () => this.startHeartbeat()
    this.ws.onmessage = (evt) => {
      const msg = JSON.parse(evt.data || '{}')
      ;(this.handlers[msg.type] || []).forEach(fn => fn(msg))
    }
    this.ws.onclose = () => {
      clearInterval(this.heartbeatTimer)
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
