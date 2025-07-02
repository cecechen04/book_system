<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">图书管理</h1>
      
      <div class="management-header flex-between mb-4">
        <div class="search-section">
          <input
            v-model="searchKeyword"
            type="text"
            class="form-input"
            placeholder="搜索图书..."
            @keyup.enter="searchBooks"
          />
          <button @click="searchBooks" class="btn btn-primary ml-2">搜索</button>
        </div>
        <button @click="showAddModal = true" class="btn btn-success">添加图书</button>
      </div>
      
      <div class="card">
        <div class="card-body">
          <div v-if="loading" class="text-center">
            <span class="loading"></span> 加载中...
          </div>
          
          <div v-else-if="books.length === 0" class="text-center text-gray-600">
            暂无图书
          </div>
          
          <div v-else class="books-table">
            <div class="table-header">
              <span>书名</span>
              <span>作者</span>
              <span>价格</span>
              <span>库存</span>
              <span>操作</span>
            </div>
            
            <div 
              v-for="book in books" 
              :key="book.id"
              class="table-row"
            >
              <span class="font-medium">{{ book.title }}</span>
              <span>{{ book.author }}</span>
              <span class="text-blue-500 font-bold">¥{{ book.price }}</span>
              <span :class="book.stock > 0 ? 'text-green-500' : 'text-red-500'">
                {{ book.stock }}
              </span>
              <span class="action-buttons">
                <button @click="editBook(book)" class="btn btn-secondary btn-sm">编辑</button>
                <button @click="deleteBook(book.id)" class="btn btn-danger btn-sm">删除</button>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑图书模态框 -->
    <div v-if="showAddModal || editingBook" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3 class="text-lg font-bold mb-4">
          {{ editingBook ? '编辑' : '添加' }}图书
        </h3>
        
        <form @submit.prevent="saveBook">
          <div class="form-group">
            <label class="form-label">书名</label>
            <input v-model="bookForm.title" type="text" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label class="form-label">作者</label>
            <input v-model="bookForm.author" type="text" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label class="form-label">价格</label>
            <input v-model.number="bookForm.price" type="number" step="0.01" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label class="form-label">库存</label>
            <input v-model.number="bookForm.stock" type="number" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label class="form-label">分类</label>
            <select v-model="bookForm.categoryId" class="form-input">
              <option value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="form-label">封面图片</label>
            <ImageUpload v-model="bookForm.coverUrl" />
          </div>
          
          <div class="form-group">
            <label class="form-label">图书简介</label>
            <textarea 
              v-model="bookForm.description" 
              class="form-input" 
              rows="4" 
              placeholder="请输入图书简介..."
            ></textarea>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { bookService } from '@/services/book'
import { categoryService } from '@/services/category'
import Header from '@/components/common/Header.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'

export default {
  name: 'BookManagement',
  components: { Header, ImageUpload },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const books = ref([])
    const categories = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    const searchKeyword = ref('')
    const showAddModal = ref(false)
    const editingBook = ref(null)
    
    const bookForm = reactive({
      title: '',
      author: '',
      price: 0,
      stock: 0,
      categoryId: '',
      coverUrl: '',
      description: ''
    })
    
    const loadBooks = async () => {
      loading.value = true
      try {
        let response
        if (searchKeyword.value.trim()) {
          response = await bookService.searchBooks(searchKeyword.value.trim())
        } else {
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
    
    const searchBooks = () => {
      loadBooks()
    }
    
    const editBook = (book) => {
      editingBook.value = book
      Object.assign(bookForm, book)
    }
    
    const closeModal = () => {
      showAddModal.value = false
      editingBook.value = null
      Object.assign(bookForm, {
        title: '',
        author: '',
        price: 0,
        stock: 0,
        categoryId: '',
        coverUrl: '',
        description: ''
      })
    }
    
    const saveBook = async () => {
      submitting.value = true
      try {
        let response
        if (editingBook.value) {
          response = await bookService.updateBook(editingBook.value.id, bookForm)
        } else {
          response = await bookService.addBook(bookForm)
        }
        
        if (response.success) {
          alert(response.message || '操作成功')
          closeModal()
          await loadBooks()
        } else {
          alert(response.message || '操作失败')
        }
      } catch (error) {
        alert('操作失败: ' + (error.message || '未知错误'))
      } finally {
        submitting.value = false
      }
    }
    
    const deleteBook = async (id) => {
      if (!confirm('确定要删除这本图书吗？')) return
      
      try {
        const response = await bookService.deleteBook(id)
        if (response.success) {
          alert('删除成功')
          await loadBooks()
        } else {
          alert(response.message || '删除失败')
        }
      } catch (error) {
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    }
    
    onMounted(() => {
      if (!authStore.isAuthenticated || !authStore.isAdmin) {
        router.push('/login')
        return
      }
      
      loadBooks()
      loadCategories()
    })
    
    return {
      books,
      categories,
      loading,
      submitting,
      searchKeyword,
      showAddModal,
      editingBook,
      bookForm,
      searchBooks,
      editBook,
      closeModal,
      saveBook,
      deleteBook
    }
  }
}
</script>

<style scoped>
.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.search-section {
  display: flex;
  gap: 0.5rem;
  flex: 1;
  max-width: 400px;
}

.books-table {
  width: 100%;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1fr 1.5fr;
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

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .management-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-section {
    max-width: none;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .action-buttons {
    justify-content: flex-start;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>