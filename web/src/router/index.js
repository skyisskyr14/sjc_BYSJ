import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'
import { checkUserAuth } from '@/api/auth'

Vue.use(VueRouter)

const routes = [
  { path: '/login', component: () => import('@/views/LoginView.vue') },
  { path: '/register', component: () => import('@/views/RegisterView.vue') },
  {
    path: '/', component: MainLayout, redirect: '/dashboard', children: [
      { path: '/dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },
      { path: '/inventory/list', component: () => import('@/views/inventory/InventoryView.vue') },
      { path: '/alert/list', component: () => import('@/views/alert/AlertListView.vue') },
      { path: '/task/list', component: () => import('@/views/task/TaskListView.vue') }
    ]
  },
  { path: '/403', component: () => import('@/views/ForbiddenView.vue') }
]

const router = new VueRouter({ routes })

router.beforeEach(async (to, from, next) => {
  if (['/login', '/register'].includes(to.path)) return next()
  try {
    const res = await checkUserAuth()
    if (res.data === 'yes') return next()
    return next('/login')
  } catch (e) {
    return next('/login')
  }
})

export default router
