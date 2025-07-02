<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">订单管理</h1>
      
      <div class="management-header mb-4">
        <div class="filter-section">
          <select v-model="selectedStatus" @change="filterOrders" class="form-input">
            <option value="">所有状态</option>
            <option value="pending">待支付</option>
            <option value="paid">已支付</option>
            <option value="shipped">已发货</option>
            <option value="delivered">已完成</option>
          </select>
        </div>
      </div>
      
      <div class="card">
        <div class="card-body">
          <div v-if="loading" class="text-center">
            <span class="loading"></span> 加载中...
          </div>
          
          <div v-else-if="orders.length === 0" class="text-center text-gray-600">
            暂无订单
          </div>
          
          <div v-else class="orders-table">
            <div class="table-header">
              <span>订单号</span>
              <span>用户ID</span>
              <span>金额</span>
              <span>状态</span>
              <span>操作</span>
            </div>
            
            <div 
              v-for="order in orders" 
              :key="order.id"
              class="table-row"
            >
              <span class="font-medium">{{ order.orderNo }}</span>
              <span>{{ order.userId }}</span>
              <span class="text-blue-500 font-bold">¥{{ order.totalAmount }}</span>
              <span>
                <span :class="getStatusClass(order.status)">
                  {{ getStatusText(order.status) }}
                </span>
              </span>
              <span class="action-buttons">
                <button @click="viewOrderDetails(order.id)" class="btn btn-secondary btn-sm">
                  查看详情
                </button>
                <button 
                  v-if="order.status === 'paid' || order.status === 'PAID'"
                  @click="shipOrder(order.id)"
                  class="btn btn-primary btn-sm"
                  :disabled="actionLoading"
                >
                  发货
                </button>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 订单详情模态框 -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="closeDetailsModal">
      <div class="modal-content" @click.stop>
        <h3 class="text-lg font-bold mb-4">订单详情</h3>
        
        <div v-if="selectedOrder" class="order-details">
          <div class="detail-row">
            <span class="detail-label">订单号：</span>
            <span>{{ selectedOrder.orderNo }}</span>
          </div>
          
          <div class="detail-row">
            <span class="detail-label">用户ID：</span>
            <span>{{ selectedOrder.userId }}</span>
          </div>
          
          <div class="detail-row">
            <span class="detail-label">订单状态：</span>
            <span :class="getStatusClass(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </span>
          </div>
          
          <div class="detail-row">
            <span class="detail-label">订单总额：</span>
            <span class="text-lg font-bold text-blue-500">¥{{ selectedOrder.totalAmount }}</span>
          </div>
          
          <div v-if="orderDetails.length > 0" class="order-items mt-4">
            <h4 class="font-bold mb-2">商品详情：</h4>
            <div 
              v-for="detail in orderDetails" 
              :key="detail.id"
              class="admin-order-item-card"
            >
              <div class="book-image-admin">
                <img 
                  v-if="detail.coverUrl" 
                  :src="detail.coverUrl" 
                  :alt="detail.bookTitle"
                  class="book-cover-admin"
                  @error="handleImageError"
                />
                <div v-else class="book-cover-fallback-admin">📚</div>
              </div>
              <div class="book-info-admin">
                <div class="book-title-admin">{{ detail.bookTitle || '未知图书' }}</div>
                <div class="book-author-admin" v-if="detail.bookAuthor">作者：{{ detail.bookAuthor }}</div>
                <div class="order-details-admin">
                  <span>数量：{{ detail.quantity }}</span>
                  <span>单价：¥{{ detail.price }}</span>
                  <span class="font-bold text-green-600">小计：¥{{ (detail.quantity * detail.price).toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="closeDetailsModal" class="btn btn-secondary">关闭</button>
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
  name: 'OrderManagement',
  components: { Header },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const orders = ref([])
    const orderDetails = ref([])
    const loading = ref(false)
    const actionLoading = ref(false)
    const selectedStatus = ref('')
    const showDetailsModal = ref(false)
    const selectedOrder = ref(null)
    
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
    
    const handleImageError = (event) => {
      event.target.style.display = 'none'
      const fallback = event.target.parentElement.querySelector('.book-cover-fallback-admin')
      if (fallback) {
        fallback.style.display = 'flex'
      }
    }
    
    const loadOrders = async () => {
      loading.value = true
      try {
        let response
        if (selectedStatus.value) {
          response = await orderService.getOrdersByStatus(selectedStatus.value)
        } else {
          response = await orderService.getAllOrders()
        }
        
        if (response.success) {
          orders.value = response.data || []
        }
      } catch (error) {
        console.error('加载订单失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const filterOrders = () => {
      loadOrders()
    }
    
    const viewOrderDetails = async (orderId) => {
      try {
        const [orderResponse, detailsResponse] = await Promise.all([
          orderService.getOrderById(orderId),
          orderService.getOrderDetailsWithBook(orderId)
        ])
        
        if (orderResponse.success) {
          selectedOrder.value = orderResponse.data
        }
        
        if (detailsResponse.success) {
          orderDetails.value = detailsResponse.data || []
        }
        
        showDetailsModal.value = true
      } catch (error) {
        console.error('加载订单详情失败:', error)
        alert('加载订单详情失败')
      }
    }
    
    
    const shipOrder = async (orderId) => {
      if (!confirm('确认发货？')) return
      
      actionLoading.value = true
      try {
        const response = await orderService.shipOrder(orderId)
        if (response.success) {
          alert('发货成功')
          // 更新订单状态
          const order = orders.value.find(o => o.id === orderId)
          if (order) {
            order.status = 'shipped'
          }
        } else {
          alert(response.message || '发货失败')
        }
      } catch (error) {
        alert('发货失败: ' + (error.message || '未知错误'))
      } finally {
        actionLoading.value = false
      }
    }
    
    const closeDetailsModal = () => {
      showDetailsModal.value = false
      selectedOrder.value = null
      orderDetails.value = []
    }
    
    onMounted(() => {
      if (!authStore.isAuthenticated || !authStore.isAdmin) {
        router.push('/login')
        return
      }
      
      loadOrders()
    })
    
    return {
      orders,
      orderDetails,
      loading,
      actionLoading,
      selectedStatus,
      showDetailsModal,
      selectedOrder,
      getStatusText,
      getStatusClass,
      filterOrders,
      viewOrderDetails,
      shipOrder,
      closeDetailsModal,
      handleImageError
    }
  }
}
</script>

<style scoped>
.management-header {
  display: flex;
  align-items: center;
}

.filter-section select {
  min-width: 150px;
}

.orders-table {
  width: 100%;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1.5fr;
  gap: 1rem;
  padding: 1rem 0;
  align-items: center;
}

.table-header {
  font-weight: bold;
  border-bottom: 2px solid #e5e7eb;
}

.table-row {
  border-bottom: 1px solid #e5e7eb;
}

.table-row:last-child {
  border-bottom: none;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.status-pending {
  background-color: #fef3c7;
  color: #d97706;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.status-paid {
  background-color: #dbeafe;
  color: #2563eb;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.status-shipped {
  background-color: #e0e7ff;
  color: #7c3aed;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.status-delivered {
  background-color: #d1fae5;
  color: #059669;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 0.5rem;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.detail-row {
  display: flex;
  margin-bottom: 0.5rem;
}

.detail-label {
  font-weight: 500;
  min-width: 100px;
}

.order-items {
  background-color: #f9fafb;
  padding: 1rem;
  border-radius: 0.375rem;
}

.item-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 1rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.item-row:last-child {
  border-bottom: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.admin-order-item-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  margin-bottom: 0.75rem;
  background: white;
}

.book-image-admin {
  flex-shrink: 0;
  width: 50px;
  height: 66px;
  position: relative;
}

.book-cover-admin {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0.25rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.book-cover-fallback-admin {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  border-radius: 0.25rem;
  font-size: 1.25rem;
  color: #6b7280;
}

.book-info-admin {
  flex: 1;
  min-width: 0;
}

.book-title-admin {
  font-weight: 600;
  font-size: 0.9rem;
  color: #374151;
  margin-bottom: 0.25rem;
  line-height: 1.3;
}

.book-author-admin {
  font-size: 0.8rem;
  color: #6b7280;
  margin-bottom: 0.5rem;
}

.order-details-admin {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #4b5563;
}

@media (max-width: 768px) {
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .action-buttons {
    justify-content: flex-start;
  }
  
  .item-row {
    grid-template-columns: 1fr;
    gap: 0.25rem;
  }
  
  .detail-row {
    flex-direction: column;
  }
}
</style>