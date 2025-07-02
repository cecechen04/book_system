<template>
  <div class="register-container">
    <div class="register-card card">
      <div class="card-header">
        <h1 class="text-center">注册</h1>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              required
              @blur="validateUsername"
            />
            <div v-if="usernameError" class="error-message">
              {{ usernameError }}
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
              @blur="validatePassword"
            />
            <div v-if="passwordError" class="error-message">
              {{ passwordError }}
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">确认密码</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="form-input"
              placeholder="请再次输入密码"
              required
              @blur="validateConfirmPassword"
            />
            <div v-if="confirmPasswordError" class="error-message">
              {{ confirmPasswordError }}
            </div>
          </div>
          
          <div v-if="error" class="error-message mb-4">
            {{ error }}
          </div>
          
          <div v-if="successMessage" class="success-message mb-4">
            {{ successMessage }}
          </div>
          
          <button 
            type="submit" 
            class="btn btn-primary"
            :disabled="loading || !isFormValid"
            style="width: 100%"
          >
            <span v-if="loading" class="loading"></span>
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
        
        <div class="text-center mt-4">
          <router-link to="/login" class="nav-link">
            已有账户？立即登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const form = reactive({
      username: '',
      password: '',
      confirmPassword: ''
    })
    
    const usernameError = ref('')
    const passwordError = ref('')
    const confirmPasswordError = ref('')
    const successMessage = ref('')
    
    const loading = computed(() => authStore.loading)
    const error = computed(() => authStore.error)
    
    const isFormValid = computed(() => {
      return form.username && 
             form.password && 
             form.confirmPassword &&
             !usernameError.value &&
             !passwordError.value &&
             !confirmPasswordError.value
    })
    
    const validateUsername = () => {
      if (!form.username) {
        usernameError.value = '用户名不能为空'
      } else if (form.username.length < 3) {
        usernameError.value = '用户名至少3个字符'
      } else {
        usernameError.value = ''
      }
    }
    
    const validatePassword = () => {
      if (!form.password) {
        passwordError.value = '密码不能为空'
      } else if (form.password.length < 6) {
        passwordError.value = '密码至少6个字符'
      } else {
        passwordError.value = ''
      }
    }
    
    const validateConfirmPassword = () => {
      if (!form.confirmPassword) {
        confirmPasswordError.value = '请确认密码'
      } else if (form.password !== form.confirmPassword) {
        confirmPasswordError.value = '两次输入的密码不一致'
      } else {
        confirmPasswordError.value = ''
      }
    }
    
    const handleRegister = async () => {
      // 清除之前的消息
      successMessage.value = ''
      authStore.clearError()
      
      // 验证表单
      validateUsername()
      validatePassword()
      validateConfirmPassword()
      
      if (!isFormValid.value) {
        return
      }
      
      const result = await authStore.register(form.username, form.password)
      
      if (result.success) {
        successMessage.value = '注册成功！即将跳转到登录页面...'
        setTimeout(() => {
          router.push('/login')
        }, 2000)
      }
    }
    
    return {
      form,
      loading,
      error,
      usernameError,
      passwordError,
      confirmPasswordError,
      successMessage,
      isFormValid,
      validateUsername,
      validatePassword,
      validateConfirmPassword,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8fafc;
}

.register-card {
  width: 100%;
  max-width: 400px;
  margin: 20px;
}

.card-header h1 {
  margin: 0;
  color: #333;
}
</style>