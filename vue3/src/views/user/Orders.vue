<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">我的订单</h1>
      
      <div v-if="loading" class="text-center">
        <span class="loading"></span> 加载中...
      </div>
      
      <div v-else-if="orders.length === 0" class="empty-orders text-center">
        <p class="text-gray-600 mb-4">还没有订单</p>
        <router-link to="/books" class="btn btn-primary">
          去买点什么
        </router-link>
      </div>
      
      <div v-else class="orders-list">
        <div 
          v-for="order in orders" 
          :key="order.id"
          class="order-card card mb-4"
        >
          <div class="card-header">
            <div class="flex-between">
              <div>
                <h3 class="font-bold">订单号：{{ order.orderNo }}</h3>
                <p class="text-sm text-gray-600">订单总额：¥{{ order.totalAmount }}</p>
              </div>
              <div class="order-status">
                <span :class="getStatusClass(order.status)">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <!-- 订单详情 -->
            <div v-if="orderDetails[order.id]" class="order-items">
              <h4 class="font-medium mb-2">商品详情：</h4>
              <div 
                v-for="detail in orderDetails[order.id]" 
                :key="detail.id"
                class="order-item-card"
              >
                <div class="book-image">
                  <img 
                    v-if="detail.coverUrl" 
                    :src="detail.coverUrl" 
                    :alt="detail.bookTitle"
                    class="book-cover-small"
                    @error="handleImageError"
                  />
                  <div v-else class="book-cover-fallback">📚</div>
                </div>
                <div class="book-info">
                  <div class="book-title">{{ detail.bookTitle || '未知图书' }}</div>
                  <div class="book-author" v-if="detail.bookAuthor">作者：{{ detail.bookAuthor }}</div>
                  <div class="order-details">
                    <span class="quantity">数量：{{ detail.quantity }}</span>
                    <span class="price">单价：¥{{ detail.price }}</span>
                    <span class="subtotal">小计：¥{{ (detail.price * detail.quantity).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 订单操作 -->
            <div class="order-actions mt-3">
              <button 
                @click="loadOrderDetails(order.id)"
                class="btn btn-secondary btn-sm"
                :disabled="detailLoading"
              >
                {{ orderDetails[order.id] ? '隐藏详情' : '查看详情' }}
              </button>
              
              <button 
                v-if="order.status === 'pending' || order.status === 'PENDING'"
                @click="payOrder(order.id)"
                class="btn btn-success btn-sm"
                :disabled="actionLoading"
              >
                <span v-if="actionLoading" class="loading"></span>
                {{ actionLoading ? '支付中...' : '立即支付' }}
              </button>
              
              <button 
                v-if="order.status === 'shipped' || order.status === 'SHIPPED'"
                @click="confirmDelivery(order.id)"
                class="btn btn-primary btn-sm"
                :disabled="actionLoading"
              >
                <span v-if="actionLoading" class="loading"></span>
                {{ actionLoading ? '确认中...' : '确认收货' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { orderService } from '@/services/order'
import Header from '@/components/common/Header.vue'

export default {
  name: 'Orders',
  components: {
    Header
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const orders = ref([])
    const orderDetails = reactive({})
    const loading = ref(false)
    const detailLoading = ref(false)
    const actionLoading = ref(false)
    
    const statusMap = {
      pending: { text: '待支付', class: 'status-pending' },
      paid: { text: '已支付', class: 'status-paid' },
      shipped: { text: '已发货', class: 'status-shipped' },
      delivered: { text: '已完成', class: 'status-delivered' },
      PENDING: { text: '待支付', class: 'status-pending' },
      PAID: { text: '已支付', class: 'status-paid' },
      SHIPPED: { text: '已发货', class: 'status-shipped' },
      DELIVERED: { text: '已完成', class: 'status-delivered' }
    }
    
    const getStatusText = (status) => {
      return statusMap[status]?.text || status
    }
    
    const getStatusClass = (status) => {
      return statusMap[status]?.class || 'status-default'
    }
    
    
    const loadOrders = async () => {
      loading.value = true
      try {
        // 确保用户已登录
        if (!authStore.user || !authStore.user.id) {
          console.error('用户信息不完整:', authStore.user)
          router.push('/login')
          return
        }
        
        const response = await orderService.getUserOrders(authStore.user.id)
        if (response.success) {
          orders.value = response.data || []
        }
      } catch (error) {
        console.error('加载订单失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const loadOrderDetails = async (orderId) => {
      // 如果已有详情，则隐藏
      if (orderDetails[orderId]) {
        delete orderDetails[orderId]
        return
      }
      
      detailLoading.value = true
      try {
        const response = await orderService.getOrderDetailsWithBook(orderId)
        if (response.success) {
          orderDetails[orderId] = response.data || []
        }
      } catch (error) {
        console.error('加载订单详情失败:', error)
      } finally {
        detailLoading.value = false
      }
    }
    
    const handleImageError = (event) => {
      event.target.style.display = 'none'
      const fallback = event.target.parentElement.querySelector('.book-cover-fallback')
      if (fallback) {
        fallback.style.display = 'flex'
      }
    }
    
    const payOrder = async (orderId) => {
      actionLoading.value = true
      try {
        const response = await orderService.payOrder(orderId)
        if (response.success) {
          alert('支付成功！')
          // 更新订单状态
          const order = orders.value.find(o => o.id === orderId)
          if (order) {
            order.status = 'paid'
          }
        } else {
          alert(response.message || '支付失败')
        }
      } catch (error) {
        alert('支付失败: ' + (error.message || '未知错误'))
      } finally {
        actionLoading.value = false
      }
    }
    
    const confirmDelivery = async (orderId) => {
      if (!confirm('确认已收到商品？')) return
      
      actionLoading.value = true
      try {
        const response = await orderService.deliverOrder(orderId)
        if (response.success) {
          alert('确认收货成功！')
          // 更新订单状态
          const order = orders.value.find(o => o.id === orderId)
          if (order) {
            order.status = 'delivered'
          }
        } else {
          alert(response.message || '确认收货失败')
        }
      } catch (error) {
        alert('确认收货失败: ' + (error.message || '未知错误'))
      } finally {
        actionLoading.value = false
      }
    }
    
    onMounted(() => {
      loadOrders()
    })
    
    return {
      orders,
      orderDetails,
      loading,
      detailLoading,
      actionLoading,
      getStatusText,
      getStatusClass,
      loadOrderDetails,
      payOrder,
      confirmDelivery,
      handleImageError
    }
  }
}
</script>

<style scoped>
.order-status span {
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-pending {
  background-color: #fef3c7;
  color: #d97706;
}

.status-paid {
  background-color: #dbeafe;
  color: #2563eb;
}

.status-shipped {
  background-color: #e0e7ff;
  color: #7c3aed;
}

.status-delivered {
  background-color: #d1fae5;
  color: #059669;
}

.status-default {
  background-color: #f3f4f6;
  color: #6b7280;
}

.order-item-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  margin-bottom: 0.75rem;
  background: #fafafa;
}

.book-image {
  flex-shrink: 0;
  width: 60px;
  height: 80px;
  position: relative;
}

.book-cover-small {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0.25rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.book-cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  border-radius: 0.25rem;
  font-size: 1.5rem;
  color: #6b7280;
}

.book-info {
  flex: 1;
  min-width: 0;
}

.book-title {
  font-weight: 600;
  font-size: 1rem;
  color: #374151;
  margin-bottom: 0.25rem;
  line-height: 1.4;
}

.book-author {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.5rem;
}

.order-details {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #4b5563;
}

.quantity, .price {
  color: #6b7280;
}

.subtotal {
  font-weight: 600;
  color: #059669;
}

.order-items {
  background-color: #f9fafb;
  padding: 1rem;
  border-radius: 0.375rem;
  margin-bottom: 1rem;
}

.order-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 1rem;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.order-item:last-child {
  border-bottom: none;
}

.order-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.empty-orders {
  padding: 4rem 0;
}

@media (max-width: 768px) {
  .flex-between {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .order-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .order-actions {
    flex-direction: column;
  }
}
</style>