<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">图书列表</h1>
      
      <!-- 搜索和筛选 -->
      <div class="filter-section card mb-4">
        <div class="card-body">
          <div class="search-filters">
            <div class="search-input-wrapper">
              <input
                v-model="searchKeyword"
                type="text"
                class="form-input search-input"
                placeholder="搜索图书名称或作者..."
                @keyup.enter="handleSearch"
              />
            </div>
            <div class="category-select-wrapper">
              <select v-model="selectedCategory" @change="handleCategoryFilter" class="form-input category-select">
                <option value="">所有分类</option>
                <option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :value="category.id"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
            <div class="search-button-wrapper">
              <button @click="handleSearch" class="btn btn-primary search-btn">
                搜索
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 图书列表 -->
      <div v-if="loading" class="text-center">
        <span class="loading"></span> 加载中...
      </div>
      
      <div v-else-if="books.length === 0" class="text-center text-gray-600">
        没有找到相关图书
      </div>
      
      <div v-else class="books-grid">
        <div 
          v-for="book in books" 
          :key="book.id"
          class="book-card card"
        >
          <div class="card-body">
            <div class="book-image">
              <img 
                v-if="book.coverUrl" 
                :src="book.coverUrl" 
                :alt="book.title" 
                class="book-cover-img"
                @error="handleImageError"
              />
              <div v-else class="book-cover fallback-cover">📚</div>
            </div>
            <h3 class="font-bold mb-2">{{ book.title }}</h3>
            <p class="text-gray-600 mb-1">作者：{{ book.author }}</p>
            <p class="text-gray-600 mb-1">库存：{{ book.stock }}</p>
            <p class="text-lg font-bold text-blue-500 mb-3">¥{{ book.price }}</p>
            
            <div class="flex gap-2">
              <router-link 
                :to="`/book/${book.id}`"
                class="btn btn-secondary btn-sm"
              >
                查看详情
              </router-link>
              <button 
                v-if="isAuthenticated && !isAdmin"
                @click="addToCart(book.id)"
                class="btn btn-primary btn-sm"
                :disabled="book.stock <= 0"
              >
                {{ book.stock > 0 ? '加入购物车' : '库存不足' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { bookService } from '@/services/book'
import { categoryService } from '@/services/category'
import Header from '@/components/common/Header.vue'

export default {
  name: 'BookList',
  components: {
    Header
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    
    const books = ref([])
    const categories = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')
    const selectedCategory = ref('')
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    
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
    
    const loadBooks = async () => {
      loading.value = true
      try {
        let response
        
        if (searchKeyword.value) {
          // 搜索图书
          response = await bookService.searchBooks(searchKeyword.value)
        } else if (selectedCategory.value) {
          // 按分类筛选
          response = await bookService.getBooksByCategory(selectedCategory.value)
        } else {
          // 获取所有图书
          response = await bookService.getAllBooks()
        }
        
        if (response.success) {
          books.value = response.data || []
        }
      } catch (error) {
        console.error('加载图书失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      selectedCategory.value = ''
      loadBooks()
    }
    
    const handleCategoryFilter = () => {
      searchKeyword.value = ''
      loadBooks()
    }
    
    const addToCart = async (bookId) => {
      if (!isAuthenticated.value) {
        router.push('/login')
        return
      }
      
      const result = await cartStore.addToCart(authStore.user.id, bookId, 1)
      if (result.success) {
        alert('添加到购物车成功！')
      } else {
        alert(result.message || '添加失败')
      }
    }
    
    const handleImageError = (event) => {
      event.target.style.display = 'none'
      const fallback = event.target.parentElement.querySelector('.book-cover')
      if (fallback) {
        fallback.style.display = 'flex'
      }
    }
    
    // 监听路由参数变化
    watch(
      () => route.query,
      (newQuery) => {
        if (newQuery.search) {
          searchKeyword.value = newQuery.search
          selectedCategory.value = ''
        } else if (newQuery.category) {
          selectedCategory.value = newQuery.category
          searchKeyword.value = ''
        }
        loadBooks()
      },
      { immediate: true }
    )
    
    onMounted(() => {
      authStore.initAuth()
      loadCategories()
    })
    
    return {
      books,
      categories,
      loading,
      searchKeyword,
      selectedCategory,
      isAuthenticated,
      isAdmin,
      handleSearch,
      handleCategoryFilter,
      addToCart,
      handleImageError
    }
  }
}
</script>

<style scoped>
.filter-section {
  background: white;
}

.search-filters {
  display: grid;
  grid-template-columns: 2fr 1fr auto;
  gap: 1rem;
  align-items: center;
}

.search-input-wrapper {
  width: 100%;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 1rem;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.category-select-wrapper {
  width: 100%;
}

.category-select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 1rem;
  background-color: white;
}

.category-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-button-wrapper {
  width: auto;
}

.search-btn {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  white-space: nowrap;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.book-card {
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-2px);
}

.book-image {
  margin-bottom: 1rem;
  text-align: center;
}

.book-cover-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-cover {
  width: 100%;
  height: 180px;
  display: none; /* 只有在图片加载失败时才显示 */
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 3rem;
  border-radius: 0.5rem;
}

.fallback-cover {
  display: flex; /* v-else时显示的fallback封面 */
}

@media (max-width: 768px) {
  .search-filters {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .books-grid {
    grid-template-columns: 1fr;
  }
}
</style>