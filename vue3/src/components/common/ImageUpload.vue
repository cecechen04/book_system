<template>
  <div class="image-upload">
    <div class="upload-area" @click="triggerFileInput" @drop="handleDrop" @dragover.prevent @dragenter.prevent>
      <div v-if="!imageUrl && !uploading" class="upload-placeholder">
        <div class="upload-icon">📷</div>
        <p>点击或拖拽上传图片</p>
        <p class="upload-tips">支持 JPG、PNG，最大 5MB</p>
      </div>
      
      <div v-if="uploading" class="upload-loading">
        <span class="loading"></span>
        <p>上传中...</p>
      </div>
      
      <div v-if="imageUrl && !uploading" class="image-preview">
        <img :src="imageUrl" :alt="fileName" />
        <div class="image-actions">
          <button @click.stop="removeImage" class="btn btn-danger btn-sm">删除</button>
          <button @click.stop="triggerFileInput" class="btn btn-secondary btn-sm">更换</button>
        </div>
      </div>
    </div>
    
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      @change="handleFileSelect"
      style="display: none"
    />
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'ImageUpload',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const fileInput = ref(null)
    const imageUrl = ref(props.modelValue)
    const fileName = ref('')
    const uploading = ref(false)
    
    // 监听外部值变化
    watch(() => props.modelValue, (newValue) => {
      imageUrl.value = newValue
    })
    
    const triggerFileInput = () => {
      fileInput.value?.click()
    }
    
    const handleFileSelect = (event) => {
      const file = event.target.files[0]
      if (file) {
        uploadFile(file)
      }
    }
    
    const handleDrop = (event) => {
      event.preventDefault()
      const file = event.dataTransfer.files[0]
      if (file && file.type.startsWith('image/')) {
        uploadFile(file)
      }
    }
    
    const uploadFile = async (file) => {
      // 文件大小验证
      if (file.size > 5 * 1024 * 1024) {
        alert('文件大小不能超过5MB')
        return
      }
      
      // 文件类型验证
      if (!file.type.startsWith('image/')) {
        alert('只能上传图片文件')
        return
      }
      
      uploading.value = true
      fileName.value = file.name
      
      try {
        const formData = new FormData()
        formData.append('file', file)
        
        const response = await axios.post('http://localhost:8080/api/upload/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.success) {
          imageUrl.value = response.data.data.url
          emit('update:modelValue', response.data.data.url)
        } else {
          alert(response.data.message || '上传失败')
        }
      } catch (error) {
        console.error('上传失败:', error)
        alert('上传失败: ' + (error.response?.data?.message || error.message))
      } finally {
        uploading.value = false
      }
    }
    
    const removeImage = () => {
      imageUrl.value = ''
      fileName.value = ''
      emit('update:modelValue', '')
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }
    
    return {
      fileInput,
      imageUrl,
      fileName,
      uploading,
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      removeImage
    }
  }
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 0.5rem;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.upload-area:hover {
  border-color: #3b82f6;
  background-color: #f8fafc;
}

.upload-placeholder {
  color: #6b7280;
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.upload-tips {
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  color: #3b82f6;
}

.image-preview {
  position: relative;
  width: 100%;
  max-width: 300px;
}

.image-preview img {
  width: 100%;
  height: auto;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.image-actions {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  display: flex;
  gap: 0.5rem;
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
</style>