import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/home' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { guest: true } },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      // 公共页面
      { path: 'home', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'goods', name: 'GoodsList', component: () => import('@/views/GoodsList.vue') },
      { path: 'needs', name: 'NeedsList', component: () => import('@/views/NeedsList.vue') },
      { path: 'goods/:id', name: 'GoodsDetail', component: () => import('@/views/GoodsDetail.vue') },
      { path: 'knowledge', name: 'KnowledgeList', component: () => import('@/views/KnowledgeList.vue') },
      { path: 'knowledge/:id', name: 'KnowledgeDetail', component: () => import('@/views/KnowledgeDetail.vue') },
      { path: 'questions', name: 'QuestionList', component: () => import('@/views/QuestionList.vue') },
      { path: 'questions/:id', name: 'QuestionDetail', component: () => import('@/views/QuestionDetail.vue') },
      { path: 'experts', name: 'ExpertList', component: () => import('@/views/ExpertList.vue') },
      { path: 'experts/:name', name: 'ExpertDetail', component: () => import('@/views/ExpertDetail.vue') },
      // 需登录用户页面
      { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { auth: true } },
      { path: 'address', name: 'AddressList', component: () => import('@/views/AddressList.vue'), meta: { auth: true } },
      { path: 'my/publish', name: 'MyPublish', component: () => import('@/views/MyPublish.vue'), meta: { auth: true } },
      { path: 'my/orders', name: 'MyOrders', component: () => import('@/views/MyOrders.vue'), meta: { auth: true } },
      { path: 'finance/intention', name: 'FinanceIntention', component: () => import('@/views/FinanceIntention.vue'), meta: { auth: true } },
      { path: 'finance/loan', name: 'FinanceLoan', component: () => import('@/views/FinanceLoan.vue'), meta: { auth: true } },
      // 专家工作台
      {
        path: 'expert/manage/qa',
        name: 'ExpertQAManage',
        component: () => import('@/views/expert/QAManage.vue'),
        meta: { auth: true, role: 'expert' }
      },
      {
        path: 'expert/manage/reserve',
        name: 'ExpertReserveManage',
        component: () => import('@/views/expert/ReserveManage.vue'),
        meta: { auth: true, role: 'expert' }
      },
      {
        path: 'expert/manage/knowledge',
        name: 'ExpertKnowledgeManage',
        component: () => import('@/views/expert/KnowledgeManage.vue'),
        meta: { auth: true, role: 'expert' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { auth: true, role: 'admin' },
    children: [
      { path: 'users', name: 'AdminUserManage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'goods', name: 'AdminGoodsManage', component: () => import('@/views/admin/GoodsManage.vue') },
      { path: 'needs', name: 'AdminNeedsManage', component: () => import('@/views/admin/NeedsManage.vue') },
      { path: 'knowledge', name: 'AdminKnowledgeManage', component: () => import('@/views/admin/KnowledgeManage.vue') },
      { path: 'questions', name: 'AdminQuestionManage', component: () => import('@/views/admin/QuestionManage.vue') },
      { path: 'reservations', name: 'AdminReserveManage', component: () => import('@/views/admin/ReserveManage.vue') },
      { path: 'finance', name: 'AdminFinanceManage', component: () => import('@/views/admin/FinanceManage.vue') },
      { path: 'logs', name: 'AdminLogManage', component: () => import('@/views/admin/LogManage.vue') },
      { path: '', redirect: '/admin/users' }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫：未登录跳 /login；按 role 控制
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = !!userStore.token
  if (to.meta.auth && !isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  if (to.meta.role && to.meta.role !== userStore.role) {
    ElMessage.error('无权访问该页面')
    next({ path: '/home' })
    return
  }
  next()
})

export default router
