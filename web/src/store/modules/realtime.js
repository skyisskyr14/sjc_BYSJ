import WSClient from '@/utils/ws'

const state = {
  metrics: {},
  alerts: []
}

const mutations = {
  SET_METRICS(state, payload) { state.metrics = payload || {} },
  PUSH_ALERT(state, payload) { state.alerts.unshift(payload) }
}

const actions = {
  initWs({ commit }) {
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const ws = new WSClient(`${protocol}://${window.location.host}/api/ws/sjc`)
    ws.subscribe('INVENTORY_METRICS_UPDATED', ({ data }) => commit('SET_METRICS', data))
    ws.subscribe('ALERT_CREATED', ({ data }) => commit('PUSH_ALERT', data))
  }
}

export default { namespaced: true, state, mutations, actions }
