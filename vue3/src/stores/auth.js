import { defineStore } from 'pinia'
import { authService } from '@/services/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user,
    isAdmin: (state) => state.user?.role === 'admin',
    isUser: (state) => state.user?.role === 'user'
  },

  actions: {
    // 初始化认证状态
    initAuth() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      
      if (token && user) {
        this.token = token
        this.user = JSON.parse(user)
      }
    },

    // 登录
    async login(username, password) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authService.login(username, password)
        
        if (response.success) {
          this.user = response.data
          this.token = 'fake-token' // 简化处理，实际项目中应使用真实token
          
          // 保存到localStorage
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
          
          return { success: true, user: this.user }
        } else {
          this.error = response.message
          return { success: false, message: response.message }
        }
      } catch (error) {
        this.error = error.message || '登录失败'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    // 注册
    async register(username, password) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authService.register(username, password)
        
        if (response.success) {
          return { success: true, message: response.message }
        } else {
          this.error = response.message
          return { success: false, message: response.message }
        }
      } catch (error) {
        this.error = error.message || '注册失败'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    // 登出
    logout() {
      this.user = null
      this.token = null
      this.error = null
      
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    // 清除错误
    clearError() {
      this.error = null
    }
  }
})