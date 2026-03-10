import Vue from 'vue'
import Vuex from 'vuex'
import realtime from './modules/realtime'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: { realtime }
})
