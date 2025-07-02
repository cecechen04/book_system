import { defineStore } from 'pinia'
import { cartService } from '@/services/cart'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),

  getters: {
    itemCount: (state) => state.items.reduce((total, item) => total + item.quantity, 0),
    totalAmount: (state) => state.items.reduce((total, item) => total + (item.price * item.quantity), 0)
  },

  actions: {
    // 获取购物车
    async fetchCart(userId) {
      this.loading = true
      this.error = null
      
      try {
        const response = await cartService.getCart(userId)
        if (response.success) {
          this.items = response.data || []
        }
      } catch (error) {
        this.error = error.message || '获取购物车失败'
      } finally {
        this.loading = false
      }
    },

    // 添加到购物车
    async addToCart(userId, bookId, quantity = 1) {
      try {
        const response = await cartService.addToCart(userId, bookId, quantity)
        if (response.success) {
          await this.fetchCart(userId)
          return { success: true, message: response.message }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '添加失败' }
      }
    },

    // 更新数量
    async updateQuantity(userId, bookId, quantity) {
      try {
        const response = await cartService.updateCartQuantity(userId, bookId, quantity)
        if (response.success) {
          await this.fetchCart(userId)
          return { success: true }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '更新失败' }
      }
    },

    // 移除商品
    async removeItem(userId, bookId) {
      try {
        const response = await cartService.removeFromCart(userId, bookId)
        if (response.success) {
          await this.fetchCart(userId)
          return { success: true }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '移除失败' }
      }
    },

    // 清空购物车
    async clearCart(userId) {
      try {
        const response = await cartService.clearCart(userId)
        if (response.success) {
          this.items = []
          return { success: true }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '清空失败' }
      }
    }
  }
})