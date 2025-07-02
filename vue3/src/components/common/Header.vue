<template>
  <div>
    <header class="nav">
      <div class="container">
        <div class="flex-between">
        <div class="flex gap-4">
          <router-link to="/home" class="nav-link">
            <h2 v-if="isAdmin">📚 图书管理系统</h2>
            <h2 v-else>📚 在线书店</h2>
          </router-link>
        </div>
        
        <nav class="flex gap-2">
          <template v-if="!isAuthenticated">
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link">注册</router-link>
          </template>
          
          <template v-else-if="isAdmin">
            <router-link to="/admin/dashboard" class="nav-link">控制台</router-link>
            <router-link to="/admin/categories" class="nav-link">分类管理</router-link>
            <router-link to="/admin/books" class="nav-link">图书管理</router-link>
            <router-link to="/admin/orders" class="nav-link">订单管理</router-link>
            <button @click="handleLogout" class="btn btn-secondary btn-sm">
              退出登录
            </button>
          </template>
          
          <template v-else>
            <router-link to="/home" class="nav-link">首页</router-link>
            <router-link to="/books" class="nav-link">图书</router-link>
            <router-link to="/cart" class="nav-link">
              购物车 
              <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
            </router-link>
            <router-link to="/orders" class="nav-link">我的订单</router-link>
            <router-link to="/chat" class="nav-link">💬 AI助手</router-link>
            <span class="nav-link">欢迎，{{ user?.username }}</span>
            <button @click="handleLogout" class="btn btn-secondary btn-sm">
              退出登录
            </button>
          </template>
        </nav>
        </div>
      </div>
    </header>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

export default {
  name: 'Header',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    const user = computed(() => authStore.user)
    const cartCount = computed(() => cartStore.itemCount)
    
    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    onMounted(() => {
      // 初始化认证状态
      authStore.initAuth()
      
      // 如果是普通用户，获取购物车数据
      if (authStore.isAuthenticated && !authStore.isAdmin) {
        cartStore.fetchCart(authStore.user.id)
      }
    })
    
    return {
      isAuthenticated,
      isAdmin,
      user,
      cartCount,
      handleLogout
    }
  }
}
</script>

<style scoped>
.nav h2 {
  margin: 0;
  color: #333;
  text-decoration: none;
}

.cart-badge {
  background-color: #ef4444;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 0.75rem;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .flex-between {
    flex-direction: column;
    gap: 1rem;
  }
  
  nav {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>