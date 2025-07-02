import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Lazy load components
const Home = () => import('@/views/user/Home.vue')
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')
const BookList = () => import('@/views/user/BookList.vue')
const BookDetail = () => import('@/views/user/BookDetail.vue')
const Cart = () => import('@/views/user/Cart.vue')
const Orders = () => import('@/views/user/Orders.vue')
const Chat = () => import('@/views/user/Chat.vue')

// Admin views
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const CategoryManagement = () => import('@/views/admin/CategoryManagement.vue')
const BookManagement = () => import('@/views/admin/BookManagement.vue')
const OrderManagement = () => import('@/views/admin/OrderManagement.vue')

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guest: true }
  },
  {
    path: '/books',
    name: 'BookList',
    component: BookList
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: BookDetail,
    props: true
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart,
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    meta: { requiresAuth: true }
  },
  // Admin routes
  {
    path: '/admin',
    redirect: '/admin/dashboard'
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/categories',
    name: 'CategoryManagement',
    component: CategoryManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/books',
    name: 'BookManagement',
    component: BookManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/orders',
    name: 'OrderManagement',
    component: OrderManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 确保初始化认证状态
  authStore.initAuth()
  
  const isAuthenticated = authStore.isAuthenticated
  const userRole = authStore.user?.role

  // console.log('Navigation guard:', {
  //   to: to.path,
  //   isAuthenticated,
  //   userRole,
  //   meta: to.meta
  // })

  // Guest only routes (login, register)
  if (to.meta.guest && isAuthenticated) {
    if (userRole === 'admin') {
      return next('/admin/dashboard')
    } else {
      return next('/home')
    }
  }

  // Routes that require authentication
  if (to.meta.requiresAuth && !isAuthenticated) {
    return next('/login')
  }

  // Admin-only routes
  if (to.meta.role === 'admin' && userRole !== 'admin') {
    return next('/home')
  }

  next()
})

export default router