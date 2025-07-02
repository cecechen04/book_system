<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">管理员控制台</h1>
      
      <!-- 统计卡片 -->
      <div class="stats-grid mb-6">
        <div class="stat-card card">
          <div class="card-body text-center">
            <h3 class="text-3xl font-bold text-blue-500">{{ stats.totalBooks }}</h3>
            <p class="text-gray-600">图书总数</p>
          </div>
        </div>
        
        <div class="stat-card card">
          <div class="card-body text-center">
            <h3 class="text-3xl font-bold text-green-500">{{ stats.totalOrders }}</h3>
            <p class="text-gray-600">订单总数</p>
          </div>
        </div>
        
        <div class="stat-card card">
          <div class="card-body text-center">
            <h3 class="text-3xl font-bold text-purple-500">{{ stats.totalCategories }}</h3>
            <p class="text-gray-600">分类总数</p>
          </div>
        </div>
        
        <div class="stat-card card">
          <div class="card-body text-center">
            <h3 class="text-3xl font-bold text-red-500">{{ stats.pendingOrders }}</h3>
            <p class="text-gray-600">待处理订单</p>
          </div>
        </div>
      </div>
      
      <!-- 快捷操作 -->
      <div class="quick-actions mb-6">
        <h2 class="text-xl font-bold mb-3">快捷操作</h2>
        <div class="actions-grid">
          <router-link to="/admin/categories" class="action-card card">
            <div class="card-body text-center">
              <div class="action-icon">📚</div>
              <h3 class="font-bold">分类管理</h3>
              <p class="text-gray-600">管理图书分类</p>
            </div>
          </router-link>
          
          <router-link to="/admin/books" class="action-card card">
            <div class="card-body text-center">
              <div class="action-icon">📖</div>
              <h3 class="font-bold">图书管理</h3>
              <p class="text-gray-600">添加和管理图书</p>
            </div>
          </router-link>
          
          <router-link to="/admin/orders" class="action-card card">
            <div class="card-body text-center">
              <div class="action-icon">📋</div>
              <h3 class="font-bold">订单管理</h3>
              <p class="text-gray-600">查看和处理订单</p>
            </div>
          </router-link>
        </div>
      </div>
      
      <!-- 最近订单 -->
      <div class="recent-orders">
        <h2 class="text-xl font-bold mb-3">最近订单</h2>
        <div class="card">
          <div class="card-body">
            <div v-if="loading" class="text-center">
              <span class="loading"></span> 加载中...
            </div>
            
            <div v-else-if="recentOrders.length === 0" class="text-center text-gray-600">
              暂无订单
            </div>
            
            <div v-else class="orders-table">
              <div class="table-header">
                <span>订单号</span>
                <span>金额</span>
                <span>状态</span>
                <span>操作</span>
              </div>
              
              <div 
                v-for="order in recentOrders" 
                :key="order.id"
                class="table-row"
              >
                <span>{{ order.orderNo }}</span>
                <span>¥{{ order.totalAmount }}</span>
                <span>
                  <span :class="getStatusClass(order.status)">
                    {{ getStatusText(order.status) }}
                  </span>
                </span>
                <span>
                  <router-link 
                    :to="`/admin/orders?orderId=${order.id}`"
                    class="btn btn-primary btn-sm"
                  >
                    查看详情
                  </router-link>
                </span>
              </div>
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
import { bookService } from '@/services/book'
import { categoryService } from '@/services/category'
import Header from '@/components/common/Header.vue'

export default {
  name: 'AdminDashboard',
  components: {
    Header
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const stats = reactive({
      totalBooks: 0,
      totalOrders: 0,
      totalCategories: 0,
      pendingOrders: 0
    })
    
    const recentOrders = ref([])
    const loading = ref(false)
    
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
    
    const loadDashboardData = async () => {
      loading.value = true
      try {
        const [booksResponse, ordersResponse, level1Response, level2Response] = await Promise.all([
          bookService.getAllBooks(),
          orderService.getAllOrders(),
          categoryService.getLevel1Categories(),
          categoryService.getLevel2Categories()
        ])
        
        // 统计数据
        if (booksResponse.success) {
          stats.totalBooks = (booksResponse.data || []).length
        }
        
        if (ordersResponse.success) {
          const orders = ordersResponse.data || []
          stats.totalOrders = orders.length
          stats.pendingOrders = orders.filter(order => order.status === 'pending' || order.status === 'PENDING').length
          
          // 最近5个订单
          recentOrders.value = orders.slice(0, 5)
        }
        
        if (level1Response.success && level2Response.success) {
          stats.totalCategories = (level1Response.data || []).length + (level2Response.data || []).length
        }
      } catch (error) {
        console.error('加载控制台数据失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      if (!authStore.isAuthenticated || !authStore.isAdmin) {
        router.push('/login')
        return
      }
      
      loadDashboardData()
    })
    
    return {
      stats,
      recentOrders,
      loading,
      getStatusText,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-card {
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.action-card {
  text-decoration: none;
  transition: transform 0.2s;
}

.action-card:hover {
  transform: translateY(-2px);
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.orders-table {
  width: 100%;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
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

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .table-header span,
  .table-row span {
    padding: 0.25rem 0;
  }
}
</style>