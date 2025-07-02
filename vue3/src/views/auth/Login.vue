<template>
  <div class="login-container">
    <div class="login-card card">
      <div class="card-header">
        <h1 class="text-center">登录</h1>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              required
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
          </div>
          
          <div v-if="error" class="error-message mb-4">
            {{ error }}
          </div>
          
          <button 
            type="submit" 
            class="btn btn-primary"
            :disabled="loading"
            style="width: 100%"
          >
            <span v-if="loading" class="loading"></span>
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
        
        <div class="text-center mt-4">
          <router-link to="/register" class="nav-link">
            还没有账户？立即注册
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const form = reactive({
      username: '',
      password: ''
    })
    
    const loading = computed(() => authStore.loading)
    const error = computed(() => authStore.error)
    
    const handleLogin = async () => {
      if (!form.username || !form.password) {
        return
      }
      
      const result = await authStore.login(form.username, form.password)
      
      if (result.success) {
        // 根据用户角色跳转
        if (result.user.role === 'admin') {
          router.push('/admin/dashboard')
        } else {
          router.push('/home')
        }
      }
    }
    
    return {
      form,
      loading,
      error,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8fafc;
}

.login-card {
  width: 100%;
  max-width: 400px;
  margin: 20px;
}

.card-header h1 {
  margin: 0;
  color: #333;
}
</style>