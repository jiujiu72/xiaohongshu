import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  { path: '/note/:id', name: 'NoteDetail', component: () => import('../views/NoteDetail.vue') },
  { path: '/publish', name: 'Publish', component: () => import('../views/Publish.vue'), meta: { requiresAuth: true } },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue'), meta: { guest: true } },
  { path: '/admin', name: 'AdminDashboard', component: () => import('../views/admin/AdminDashboard.vue'), meta: { requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin) {
    if (!token || role !== 'admin') {
      next('/admin/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
