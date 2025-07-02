import api from './api'

export const orderService = {
  // 从购物车创建订单
  async createOrder(userId) {
    const response = await api.post('/orders/create', { userId })
    return response
  },

  // 获取用户订单列表
  async getUserOrders(userId) {
    const response = await api.get(`/orders/user/${userId}`)
    return response
  },

  // 获取所有订单（管理员）
  async getAllOrders() {
    const response = await api.get('/orders')
    return response
  },

  // 根据状态获取订单
  async getOrdersByStatus(status) {
    const response = await api.get(`/orders/status/${status}`)
    return response
  },

  // 获取订单详情
  async getOrderById(id) {
    const response = await api.get(`/orders/${id}`)
    return response
  },

  // 获取订单商品详情
  async getOrderDetails(id) {
    const response = await api.get(`/orders/${id}/details`)
    return response
  },

  // 获取订单商品详情（包含图书信息）
  async getOrderDetailsWithBook(id) {
    const response = await api.get(`/orders/${id}/details-with-book`)
    return response
  },

  // 支付订单
  async payOrder(id) {
    const response = await api.put(`/orders/${id}/pay`)
    return response
  },

  // 发货（管理员）
  async shipOrder(id) {
    const response = await api.put(`/orders/${id}/ship`)
    return response
  },

  // 确认收货
  async deliverOrder(id) {
    const response = await api.put(`/orders/${id}/deliver`)
    return response
  }
}