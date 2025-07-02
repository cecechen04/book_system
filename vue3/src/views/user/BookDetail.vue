<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <div v-if="loading" class="text-center">
        <span class="loading"></span> 加载中...
      </div>
      
      <div v-else-if="!book" class="text-center">
        <p class="text-gray-600">图书不存在</p>
        <router-link to="/books" class="btn btn-primary mt-3">
          返回图书列表
        </router-link>
      </div>
      
      <div v-else class="book-detail">
        <!-- 返回按钮 -->
        <div class="mb-4">
          <button @click="goBack" class="btn btn-secondary">
            ← 返回
          </button>
        </div>
        
        <!-- 图书信息 -->
        <div class="book-info card">
          <div class="card-body">
            <div class="book-detail-content">
              <div class="book-image">
                <img 
                  v-if="book.coverUrl" 
                  :src="book.coverUrl" 
                  :alt="book.title" 
                  class="book-cover-img"
                  @error="handleImageError"
                />
                <div v-else class="image-placeholder">
                  📖
                </div>
              </div>
              
              <div class="book-details">
                <h1 class="text-2xl font-bold mb-3">{{ book.title }}</h1>
                
                <div class="detail-item">
                  <span class="detail-label">作者：</span>
                  <span>{{ book.author }}</span>
                </div>
                
                <div class="detail-item">
                  <span class="detail-label">价格：</span>
                  <span class="text-2xl font-bold text-blue-500">¥{{ book.price }}</span>
                </div>
                
                <div class="detail-item">
                  <span class="detail-label">库存：</span>
                  <span :class="book.stock > 0 ? 'text-green-500' : 'text-red-500'">
                    {{ book.stock > 0 ? `${book.stock} 本` : '库存不足' }}
                  </span>
                </div>
                
                <div class="detail-item">
                  <span class="detail-label">分类：</span>
                  <span>{{ categoryName }}</span>
                </div>
                
                <div v-if="book.description" class="detail-item description-section">
                  <span class="detail-label">图书简介：</span>
                  <div class="description-content">
                    {{ book.description }}
                  </div>
                </div>
                
                <!-- 购买操作 -->
                <div v-if="isAuthenticated && !isAdmin" class="purchase-section">
                  <div class="purchase-controls">
                    <div class="quantity-selector">
                      <label class="detail-label">数量：</label>
                      <div class="quantity-controls">
                        <button 
                          @click="decreaseQuantity" 
                          class="btn btn-secondary quantity-btn"
                          :disabled="quantity <= 1"
                        >
                          -
                        </button>
                        <input 
                          v-model.number="quantity" 
                          type="number" 
                          min="1" 
                          :max="book.stock"
                          class="form-input quantity-input"
                        />
                        <button 
                          @click="increaseQuantity" 
                          class="btn btn-secondary quantity-btn"
                          :disabled="quantity >= book.stock"
                        >
                          +
                        </button>
                      </div>
                    </div>
                    
                    <button 
                      @click="addToCart"
                      class="btn btn-primary add-cart-btn"
                      :disabled="book.stock <= 0 || cartLoading"
                    >
                      <span v-if="cartLoading" class="loading"></span>
                      {{ cartLoading ? '添加中...' : '加入购物车' }}
                    </button>
                  </div>
                </div>
                
                <div v-else-if="!isAuthenticated" class="mt-4">
                  <p class="text-gray-600 mb-3">请登录后购买</p>
                  <router-link to="/login" class="btn btn-primary">
                    立即登录
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
        
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { bookService } from '@/services/book'
import { categoryService } from '@/services/category'
import Header from '@/components/common/Header.vue'

export default {
  name: 'BookDetail',
  components: {
    Header
  },
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    
    const book = ref(null)
    const categories = ref([])
    const loading = ref(false)
    const cartLoading = ref(false)
    const quantity = ref(1)
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    
    const categoryName = computed(() => {
      if (!book.value?.categoryId) return '未分类'
      const category = categories.value.find(c => c.id === book.value.categoryId)
      return category?.name || '未知分类'
    })
    
    const loadBook = async () => {
      loading.value = true
      try {
        const response = await bookService.getBookById(props.id)
        if (response.success) {
          book.value = response.data
        }
      } catch (error) {
        console.error('加载图书详情失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const loadCategories = async () => {
      try {
        const response = await categoryService.getLevel2Categories()
        if (response.success) {
          categories.value = response.data || []
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }
    
    const increaseQuantity = () => {
      if (quantity.value < book.value.stock) {
        quantity.value++
      }
    }
    
    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }
    
    const addToCart = async () => {
      if (!isAuthenticated.value) {
        router.push('/login')
        return
      }
      
      cartLoading.value = true
      const result = await cartStore.addToCart(authStore.user.id, book.value.id, quantity.value)
      cartLoading.value = false
      
      if (result.success) {
        alert(`成功添加 ${quantity.value} 本到购物车！`)
        quantity.value = 1
      } else {
        alert(result.message || '添加失败')
      }
    }
    
    const goBack = () => {
      router.back()
    }
    
    const handleImageError = (event) => {
      event.target.style.display = 'none'
      const placeholder = event.target.parentElement.querySelector('.image-placeholder')
      if (placeholder) {
        placeholder.style.display = 'flex'
      }
    }
    
    onMounted(() => {
      authStore.initAuth()
      loadBook()
      loadCategories()
    })
    
    return {
      book,
      loading,
      cartLoading,
      quantity,
      isAuthenticated,
      isAdmin,
      categoryName,
      increaseQuantity,
      decreaseQuantity,
      addToCart,
      goBack,
      handleImageError
    }
  }
}
</script>

<style scoped>
.book-detail-content {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 2rem;
}

.book-image {
  text-align: center;
}

.book-cover-img {
  width: 200px;
  height: 280px;
  object-fit: cover;
  border-radius: 0.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-placeholder {
  width: 200px;
  height: 280px;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  border-radius: 0.5rem;
  color: #9ca3af;
}

.detail-item {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
}

.detail-label {
  font-weight: 500;
  min-width: 80px;
  color: #374151;
}

.description-section {
  flex-direction: column;
  align-items: flex-start;
}

.description-section .detail-label {
  margin-bottom: 0.5rem;
}

.description-content {
  line-height: 1.6;
  color: #4b5563;
  background-color: #f9fafb;
  padding: 1rem;
  border-radius: 0.5rem;
  border-left: 4px solid #3b82f6;
}

.purchase-section {
  border-top: 1px solid #e5e7eb;
  padding-top: 1rem;
}

.purchase-controls {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  font-weight: 600;
}

.quantity-input {
  width: 60px;
  text-align: center;
  height: 32px;
  padding: 0.25rem;
}

.add-cart-btn {
  padding: 0.5rem 1.5rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

@media (max-width: 768px) {
  .book-detail-content {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .image-placeholder {
    width: 150px;
    height: 210px;
    margin: 0 auto;
    font-size: 3rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>