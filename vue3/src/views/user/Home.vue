<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <!-- 搜索框 -->
      <div class="search-section mb-6">
        <div class="search-box">
          <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="搜索图书名称或作者..."
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch" class="search-btn btn btn-primary">
            搜索
          </button>
        </div>
      </div>
      
      <!-- 分类导航 -->
      <div class="categories-section mb-6">
        <h2>图书分类</h2>
        <div v-if="loading" class="text-center">
          <span class="loading"></span> 加载中...
        </div>
        <div v-else class="categories-grid">
          <div 
            v-for="category in categories" 
            :key="category.id"
            class="category-card card"
          >
            <div class="card-body">
              <h3>{{ category.name }}</h3>
              <div class="subcategories">
                <router-link
                  v-for="subcategory in category.subcategories"
                  :key="subcategory.id"
                  :to="`/books?category=${subcategory.id}`"
                  class="subcategory-link"
                >
                  {{ subcategory.name }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 推荐图书 -->
      <div class="books-section">
        <h2>热门推荐</h2>
        <div v-if="booksLoading" class="text-center">
          <span class="loading"></span> 加载中...
        </div>
        <div v-else class="books-grid">
          <div 
            v-for="book in featuredBooks" 
            :key="book.id"
            class="book-card card"
            @click="goToBookDetail(book.id)"
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
              <h3>{{ book.title }}</h3>
              <p>作者：{{ book.author }}</p>
              <p>库存：{{ book.stock }}</p>
              <p class="price">¥{{ book.price }}</p>
              
              <div class="flex gap-2">
                <router-link 
                  :to="`/book/${book.id}`"
                  class="btn btn-secondary btn-sm"
                >
                  查看详情
                </router-link>
                <button 
                  v-if="isAuthenticated && !isAdmin"
                  @click.stop="addToCart(book.id)"
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
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { categoryService } from '@/services/category'
import { bookService } from '@/services/book'
import Header from '@/components/common/Header.vue'

export default {
  name: 'Home',
  components: {
    Header
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    
    const searchKeyword = ref('')
    const categories = ref([])
    const featuredBooks = ref([])
    const loading = ref(false)
    const booksLoading = ref(false)
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    
    const loadCategories = async () => {
      loading.value = true
      try {
        const [level1Response, level2Response] = await Promise.all([
          categoryService.getLevel1Categories(),
          categoryService.getLevel2Categories()
        ])
        
        if (level1Response.success && level2Response.success) {
          const level1Categories = level1Response.data || []
          const level2Categories = level2Response.data || []
          
          // 组织分类数据
          categories.value = level1Categories.map(category => ({
            ...category,
            subcategories: level2Categories.filter(sub => sub.parentId === category.id)
          }))
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const loadFeaturedBooks = async () => {
      booksLoading.value = true
      try {
        const response = await bookService.getAllBooks()
        if (response.success) {
          // 显示前8本书作为推荐
          featuredBooks.value = (response.data || []).slice(0, 8)
        }
      } catch (error) {
        console.error('加载图书失败:', error)
      } finally {
        booksLoading.value = false
      }
    }
    
    const handleSearch = () => {
      if (searchKeyword.value.trim()) {
        router.push(`/books?search=${encodeURIComponent(searchKeyword.value.trim())}`)
      }
    }
    
    const goToBookDetail = (bookId) => {
      router.push(`/book/${bookId}`)
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
    
    onMounted(() => {
      authStore.initAuth()
      loadCategories()
      loadFeaturedBooks()
    })
    
    return {
      searchKeyword,
      categories,
      featuredBooks,
      loading,
      booksLoading,
      isAuthenticated,
      isAdmin,
      handleSearch,
      goToBookDetail,
      addToCart,
      handleImageError
    }
  }
}
</script>

<style scoped>
.search-section {
  background: white;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.search-box {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  flex: 1;
  padding: 0.875rem 1.25rem;
  border: 2px solid #e5e7eb;
  border-radius: 0.75rem;
  font-size: 1rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-btn {
  padding: 0.875rem 1.5rem;
  font-size: 1rem;
  border-radius: 0.75rem;
  white-space: nowrap;
  font-weight: 600;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.category-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.category-card:hover {
  transform: translateY(-2px);
}

.subcategories {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 1rem;
}

.subcategory-link {
  padding: 0.25rem 0.75rem;
  background-color: #f3f4f6;
  border-radius: 1rem;
  text-decoration: none;
  color: #374151;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.subcategory-link:hover {
  background-color: #3b82f6;
  color: white;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.book-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-2px);
}

.price {
  color: #3b82f6;
  font-weight: bold;
  font-size: 1.1rem;
}

.book-image {
  margin-bottom: 1rem;
  text-align: center;
}

.book-cover-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-cover {
  width: 100%;
  height: 200px;
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

.mb-6 {
  margin-bottom: 1.5rem;
}

.text-center {
  text-align: center;
}

.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #f3f4f6;
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .search-box {
    flex-direction: column;
    gap: 1rem;
  }
  
  .search-input,
  .search-btn {
    width: 100%;
  }
  
  .categories-grid,
  .books-grid {
    grid-template-columns: 1fr;
  }
  
  .flex {
    flex-direction: column;
  }
}
</style>