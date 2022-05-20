import Vue from 'vue'
import VueRouter from 'vue-router'
import Swal from 'sweetalert2'

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
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/signup',
    name: 'Signup',
    component: () => import('../views/Signup.vue')
  },
  {
    path: '/main',
    name: 'Main',
    component: () => import('../views/Main.vue')
  },
  {
    path:'/mypage/:nickName',
    name:'Mypage',
    component: () => import('../views/MyPage.vue')
  },
  {
    path:'/diary',
    name:'DiaryCreate',
    component: () => import('../views/Diary.vue'),
    props: route => ({ diaryId: Number(route.params.diaryId) }),
    children: [
      {
        path: ':diaryId',
        name: 'DiaryDetail',
        component: () => import('../views/Diary.vue')
      }
    ]
  },
  {
    path:'/oauth',
    name:'Oauth',
    component: () => import('../views/Oauth.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

const session = window.sessionStorage

// eslint-disable-next-line no-unused-vars
router.beforeEach((to, from, next) => {

  if (to.params.forced){
    next()
  }
  
  else {
    const accessToken = session.getItem('accessToken')

    if (!accessToken) {
      console.log(to.name)
      if (to.name !== 'Login' && to.name !== 'Signup' && to.name !== 'LandingPage' && to.name !== 'Oauth'){
        Swal.fire({
          icon: 'warning',
          text: '로그인이 필요합니다'
        })
        .then(() => {
          next({name: 'Login', params: {forced: true}})
        })
      }
      else {
        next()
      }
    }

    if (to.name == 'Main' && !to.query.tab){
      next({ name: 'Main', query: { tab: 'calendar' } })
    }
  
    if ((from.name == 'DiaryCreate' || from.name == 'DiaryDetail')){
      Swal.fire({
        icon: 'warning',
        html: '저장하지 않은 정보는 사라집니다.<br> 그래도 페이지를 이동할까요?',
        showConfirmButton: true,
        showCancelButton: true
      })
      .then(res => {
        if (res.isConfirmed){
          router.push({name: to.name, query: {...to.query}, params: {forced: true}})
        }
      })
    }
  
    else {
      next()
    }
  }
})

export default router
