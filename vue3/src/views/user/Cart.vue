<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">我的购物车</h1>
      
      <div v-if="loading" class="text-center">
        <span class="loading"></span> 加载中...
      </div>
      
      <div v-else-if="cartItems.length === 0" class="empty-cart text-center">
        <p class="text-gray-600 mb-4">购物车还是空的</p>
        <router-link to="/books" class="btn btn-primary">
          去买点什么
        </router-link>
      </div>
      
      <div v-else class="cart-content">
        <!-- 购物车列表 -->
        <div class="cart-items card mb-4">
          <div class="card-header">
            <h2 class="text-xl font-bold">商品列表</h2>
          </div>
          <div class="card-body">
            <div 
              v-for="item in cartItems" 
              :key="item.id"
              class="cart-item"
            >
              <div class="item-info">
                <h3 class="font-bold">{{ getBookTitle(item.bookId) }}</h3>
                <p class="text-gray-600">{{ getBookAuthor(item.bookId) }}</p>
                <p class="text-blue-500 font-bold">¥{{ getBookPrice(item.bookId) }}</p>
              </div>
              
              <div class="item-actions">
                <div class="quantity-controls">
                  <button 
                    @click="updateQuantity(item.bookId, item.quantity - 1)"
                    class="btn btn-secondary btn-sm"
                    :disabled="item.quantity <= 1 || updating"
                  >
                    -
                  </button>
                  <span class="quantity">{{ item.quantity }}</span>
                  <button 
                    @click="updateQuantity(item.bookId, item.quantity + 1)"
                    class="btn btn-secondary btn-sm"
                    :disabled="updating"
                  >
                    +
                  </button>
                </div>
                
                <button 
                  @click="removeItem(item.bookId)"
                  class="btn btn-danger btn-sm"
                  :disabled="updating"
                >
                  移除
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 订单总计 -->
        <div class="cart-summary card">
          <div class="card-header">
            <h2 class="text-xl font-bold">订单总计</h2>
          </div>
          <div class="card-body">
            <div class="summary-row">
              <span>商品数量：</span>
              <span>{{ totalQuantity }} 件</span>
            </div>
            <div class="summary-row">
              <span>商品总价：</span>
              <span class="text-2xl font-bold text-blue-500">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            
            <div class="checkout-actions mt-4">
              <button 
                @click="createOrder"
                class="btn btn-primary"
                :disabled="creating || cartItems.length === 0"
                style="width: 100%"
              >
                <span v-if="creating" class="loading"></span>
                {{ creating ? '创建中...' : '立即结算' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { bookService } from '@/services/book'
import { orderService } from '@/services/order'
import Header from '@/components/common/Header.vue'

export default {
  name: 'Cart',
  components: {
    Header
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    
    const books = ref([])
    const loading = ref(false)
    const updating = ref(false)
    const creating = ref(false)
    
    const cartItems = computed(() => cartStore.items)
    
    const totalQuantity = computed(() => {
      return cartItems.value.reduce((total, item) => total + item.quantity, 0)
    })
    
    const totalAmount = computed(() => {
      return cartItems.value.reduce((total, item) => {
        const book = books.value.find(b => b.id === item.bookId)
        return total + (book ? book.price * item.quantity : 0)
      }, 0)
    })
    
    const getBookTitle = (bookId) => {
      const book = books.value.find(b => b.id === bookId)
      return book?.title || '未知图书'
    }
    
    const getBookAuthor = (bookId) => {
      const book = books.value.find(b => b.id === bookId)
      return book?.author || '未知作者'
    }
    
    const getBookPrice = (bookId) => {
      const book = books.value.find(b => b.id === bookId)
      return book?.price || 0
    }
    
    const loadCartData = async () => {
      loading.value = true
      try {
        // 确保用户已登录
        if (!authStore.user || !authStore.user.id) {
          console.error('用户信息不完整:', authStore.user)
          router.push('/login')
          return
        }
        
        // 获取购物车
        await cartStore.fetchCart(authStore.user.id)
        
        // 获取相关图书信息
        if (cartItems.value.length > 0) {
          const bookIds = cartItems.value.map(item => item.bookId)
          const bookPromises = bookIds.map(id => bookService.getBookById(id))
          const bookResponses = await Promise.all(bookPromises)
          
          books.value = bookResponses
            .filter(response => response.success)
            .map(response => response.data)
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const updateQuantity = async (bookId, newQuantity) => {
      if (newQuantity <= 0) return
      
      updating.value = true
      const result = await cartStore.updateQuantity(authStore.user.id, bookId, newQuantity)
      updating.value = false
      
      if (!result.success) {
        alert(result.message || '更新失败')
      }
    }
    
    const removeItem = async (bookId) => {
      if (!confirm('确定要移除这件商品吗？')) return
      
      updating.value = true
      const result = await cartStore.removeItem(authStore.user.id, bookId)
      updating.value = false
      
      if (result.success) {
        // 从本地图书列表中移除
        books.value = books.value.filter(book => book.id !== bookId)
      } else {
        alert(result.message || '移除失败')
      }
    }
    
    const createOrder = async () => {
      if (cartItems.value.length === 0) return
      
      creating.value = true
      try {
        const response = await orderService.createOrder(authStore.user.id)
        if (response.success) {
          alert(`订单创建成功！订单号：${response.data}`)
          router.push('/orders')
        } else {
          alert(response.message || '订单创建失败')
        }
      } catch (error) {
        alert('订单创建失败: ' + (error.message || '未知错误'))
      } finally {
        creating.value = false
      }
    }
    
    onMounted(() => {
      loadCartData()
    })
    
    return {
      cartItems,
      books,
      loading,
      updating,
      creating,
      totalQuantity,
      totalAmount,
      getBookTitle,
      getBookAuthor,
      getBookPrice,
      updateQuantity,
      removeItem,
      createOrder
    }
  }
}
</script>

<style scoped>
.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info h3 {
  margin-bottom: 0.5rem;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity {
  min-width: 2rem;
  text-align: center;
  font-weight: bold;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
}

.empty-cart {
  padding: 4rem 0;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .item-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>