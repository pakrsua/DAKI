import Vue from 'vue'
import VueRouter from 'vue-router'

// Landing Page
import LandingPage from '@/views/LandingPage.vue'

Vue.use(VueRouter)

const routes = [
  {
    path:'/',
    name: 'LandingPage',
    component: LandingPage
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/signup',
    name: 'signup',
    component: () => import('../views/Signup.vue')
  },
]


const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
