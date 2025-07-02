import api from './api'

export const cartService = {
  // 获取用户购物车
  async getCart(userId) {
    const response = await api.get(`/cart/user/${userId}`)
    return response
  },

  // 添加商品到购物车
  async addToCart(userId, bookId, quantity) {
    const response = await api.post('/cart/add', { userId, bookId, quantity })
    return response
  },

  // 更新购物车商品数量
  async updateCartQuantity(userId, bookId, quantity) {
    const response = await api.put('/cart/update', { userId, bookId, quantity })
    return response
  },

  // 从购物车移除商品
  async removeFromCart(userId, bookId) {
    const response = await api.delete(`/cart/remove?userId=${userId}&bookId=${bookId}`)
    return response
  },

  // 清空购物车
  async clearCart(userId) {
    const response = await api.delete(`/cart/clear/${userId}`)
    return response
  }
}