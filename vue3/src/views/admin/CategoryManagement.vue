<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <h1 class="text-2xl font-bold mb-4">分类管理</h1>
      
      <!-- 一级分类管理 -->
      <div class="level1-section mb-6">
        <div class="section-header flex-between mb-3">
          <h2 class="text-xl font-bold">一级分类</h2>
          <button @click="showAddLevel1Modal = true" class="btn btn-primary">
            添加一级分类
          </button>
        </div>
        
        <div class="card">
          <div class="card-body">
            <div v-if="loading" class="text-center">
              <span class="loading"></span> 加载中...
            </div>
            
            <div v-else-if="level1Categories.length === 0" class="text-center text-gray-600">
              暂无分类
            </div>
            
            <div v-else class="categories-list">
              <div 
                v-for="category in level1Categories" 
                :key="category.id"
                class="category-item"
              >
                <span class="category-name">{{ category.name }}</span>
                <div class="category-actions">
                  <button 
                    @click="editLevel1Category(category)" 
                    class="btn btn-secondary btn-sm"
                  >
                    编辑
                  </button>
                  <button 
                    @click="deleteLevel1Category(category.id)" 
                    class="btn btn-danger btn-sm"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 二级分类管理 -->
      <div class="level2-section">
        <div class="section-header flex-between mb-3">
          <h2 class="text-xl font-bold">二级分类</h2>
          <button 
            @click="showAddLevel2Modal = true" 
            class="btn btn-primary"
            :disabled="level1Categories.length === 0"
          >
            添加二级分类
          </button>
        </div>
        
        <div class="card">
          <div class="card-body">
            <div v-if="level2Categories.length === 0" class="text-center text-gray-600">
              暂无二级分类
            </div>
            
            <div v-else class="categories-list">
              <div 
                v-for="category in level2Categories" 
                :key="category.id"
                class="category-item"
              >
                <span class="category-name">
                  {{ category.name }} 
                  <span class="text-gray-500 text-sm">
                    ({{ getParentCategoryName(category.parentId) }})
                  </span>
                </span>
                <div class="category-actions">
                  <button 
                    @click="editLevel2Category(category)" 
                    class="btn btn-secondary btn-sm"
                  >
                    编辑
                  </button>
                  <button 
                    @click="deleteLevel2Category(category.id)" 
                    class="btn btn-danger btn-sm"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑一级分类模态框 -->
    <div v-if="showAddLevel1Modal || editingLevel1" class="modal-overlay" @click="closeLevel1Modal">
      <div class="modal-content" @click.stop>
        <h3 class="text-lg font-bold mb-4">
          {{ editingLevel1 ? '编辑' : '添加' }}一级分类
        </h3>
        
        <form @submit.prevent="saveLevel1Category">
          <div class="form-group">
            <label class="form-label">分类名称</label>
            <input 
              v-model="level1Form.name" 
              type="text" 
              class="form-input"
              placeholder="请输入分类名称"
              required
            />
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeLevel1Modal" class="btn btn-secondary">
              取消
            </button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 添加/编辑二级分类模态框 -->
    <div v-if="showAddLevel2Modal || editingLevel2" class="modal-overlay" @click="closeLevel2Modal">
      <div class="modal-content" @click.stop>
        <h3 class="text-lg font-bold mb-4">
          {{ editingLevel2 ? '编辑' : '添加' }}二级分类
        </h3>
        
        <form @submit.prevent="saveLevel2Category">
          <div class="form-group">
            <label class="form-label">分类名称</label>
            <input 
              v-model="level2Form.name" 
              type="text" 
              class="form-input"
              placeholder="请输入分类名称"
              required
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">父分类</label>
            <select v-model="level2Form.parentId" class="form-input" required>
              <option value="">请选择父分类</option>
              <option 
                v-for="category in level1Categories" 
                :key="category.id" 
                :value="category.id"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeLevel2Modal" class="btn btn-secondary">
              取消
            </button>
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
import { categoryService } from '@/services/category'
import Header from '@/components/common/Header.vue'

export default {
  name: 'CategoryManagement',
  components: {
    Header
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const level1Categories = ref([])
    const level2Categories = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    
    const showAddLevel1Modal = ref(false)
    const showAddLevel2Modal = ref(false)
    const editingLevel1 = ref(null)
    const editingLevel2 = ref(null)
    
    const level1Form = reactive({
      name: ''
    })
    
    const level2Form = reactive({
      name: '',
      parentId: ''
    })
    
    const getParentCategoryName = (parentId) => {
      const parent = level1Categories.value.find(cat => cat.id === parentId)
      return parent?.name || '未知'
    }
    
    const loadCategories = async () => {
      loading.value = true
      try {
        const [level1Response, level2Response] = await Promise.all([
          categoryService.getLevel1Categories(),
          categoryService.getLevel2Categories()
        ])
        
        if (level1Response.success) {
          level1Categories.value = level1Response.data || []
        }
        
        if (level2Response.success) {
          level2Categories.value = level2Response.data || []
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const editLevel1Category = (category) => {
      editingLevel1.value = category
      level1Form.name = category.name
    }
    
    const editLevel2Category = (category) => {
      editingLevel2.value = category
      level2Form.name = category.name
      level2Form.parentId = category.parentId
    }
    
    const closeLevel1Modal = () => {
      showAddLevel1Modal.value = false
      editingLevel1.value = null
      level1Form.name = ''
    }
    
    const closeLevel2Modal = () => {
      showAddLevel2Modal.value = false
      editingLevel2.value = null
      level2Form.name = ''
      level2Form.parentId = ''
    }
    
    const saveLevel1Category = async () => {
      submitting.value = true
      try {
        let response
        if (editingLevel1.value) {
          response = await categoryService.updateLevel1Category(editingLevel1.value.id, level1Form.name)
        } else {
          response = await categoryService.addLevel1Category(level1Form.name)
        }
        
        if (response.success) {
          alert(response.message || '操作成功')
          closeLevel1Modal()
          await loadCategories()
        } else {
          alert(response.message || '操作失败')
        }
      } catch (error) {
        alert('操作失败: ' + (error.message || '未知错误'))
      } finally {
        submitting.value = false
      }
    }
    
    const saveLevel2Category = async () => {
      submitting.value = true
      try {
        let response
        if (editingLevel2.value) {
          response = await categoryService.updateLevel2Category(
            editingLevel2.value.id, 
            level2Form.name, 
            level2Form.parentId
          )
        } else {
          response = await categoryService.addLevel2Category(level2Form.name, level2Form.parentId)
        }
        
        if (response.success) {
          alert(response.message || '操作成功')
          closeLevel2Modal()
          await loadCategories()
        } else {
          alert(response.message || '操作失败')
        }
      } catch (error) {
        alert('操作失败: ' + (error.message || '未知错误'))
      } finally {
        submitting.value = false
      }
    }
    
    const deleteLevel1Category = async (id) => {
      if (!confirm('确定要删除这个分类吗？删除后相关的二级分类也会被删除。')) return
      
      try {
        const response = await categoryService.deleteLevel1Category(id)
        if (response.success) {
          alert('删除成功')
          await loadCategories()
        } else {
          alert(response.message || '删除失败')
        }
      } catch (error) {
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    }
    
    const deleteLevel2Category = async (id) => {
      if (!confirm('确定要删除这个分类吗？')) return
      
      try {
        const response = await categoryService.deleteLevel2Category(id)
        if (response.success) {
          alert('删除成功')
          await loadCategories()
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
      
      loadCategories()
    })
    
    return {
      level1Categories,
      level2Categories,
      loading,
      submitting,
      showAddLevel1Modal,
      showAddLevel2Modal,
      editingLevel1,
      editingLevel2,
      level1Form,
      level2Form,
      getParentCategoryName,
      editLevel1Category,
      editLevel2Category,
      closeLevel1Modal,
      closeLevel2Modal,
      saveLevel1Category,
      saveLevel2Category,
      deleteLevel1Category,
      deleteLevel2Category
    }
  }
}
</script>

<style scoped>
.categories-list {
  space-y: 1rem;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.category-item:last-child {
  border-bottom: none;
}

.category-name {
  font-weight: 500;
}

.category-actions {
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
  max-width: 400px;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .category-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .category-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>