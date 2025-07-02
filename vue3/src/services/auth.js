import api from './api'

export const authService = {
  // 登录
  async login(username, password) {
    const response = await api.post('/auth/login', { username, password })
    return response
  },

  // 注册
  async register(username, password) {
    const response = await api.post('/auth/register', { username, password })
    return response
  },

  // 检查用户名是否可用
  async checkUsername(username) {
    const response = await api.get(`/auth/check-username?username=${username}`)
    return response
  }
}