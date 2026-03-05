import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

Vue.use(VueRouter)

const routes = [{
  path: '/', component: MainLayout, redirect: '/dashboard', children: [
    { path: '/dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { roles: ['SYS_ADMIN','DISPATCHER','WAREHOUSE_ADMIN','VIEWER'] } },
    { path: '/inventory/list', component: () => import('@/views/inventory/InventoryView.vue'), meta: { roles: ['SYS_ADMIN','WAREHOUSE_ADMIN'] } },
    { path: '/alert/list', component: () => import('@/views/alert/AlertListView.vue'), meta: { roles: ['SYS_ADMIN','DISPATCHER','WAREHOUSE_ADMIN','VIEWER'] } },
    { path: '/task/list', component: () => import('@/views/task/TaskListView.vue'), meta: { roles: ['SYS_ADMIN','DISPATCHER'] } }
  ]
}, { path: '/403', component: () => import('@/views/ForbiddenView.vue') }]

const router = new VueRouter({ routes })
router.beforeEach((to, from, next) => {
  const roles = JSON.parse(localStorage.getItem('sjc_roles') || '["SYS_ADMIN"]')
  const need = to.meta && to.meta.roles
  if (!need || need.some(r => roles.includes(r))) return next()
  return next('/403')
})

export default router
