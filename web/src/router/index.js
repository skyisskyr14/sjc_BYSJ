import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

Vue.use(VueRouter)

const routes = [{
  path: '/', component: MainLayout, redirect: '/dashboard', children: [
    { path: '/dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },
    { path: '/inventory/list', component: () => import('@/views/inventory/InventoryView.vue') },
    { path: '/alert/list', component: () => import('@/views/alert/AlertListView.vue') },
    { path: '/task/list', component: () => import('@/views/task/TaskListView.vue') }
  ]
}]

export default new VueRouter({ routes })
