<template>
  <div>
    <Header />
    <div class="container" style="padding: 2rem 15px;">
      <div class="chat-container">
        <div class="chat-header">
          <h2>💬 AI智能助手</h2>
          <p class="chat-subtitle">有任何问题都可以问我哦！</p>
        </div>

        <!-- 聊天消息区域 -->
        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="chat-welcome">
            <div class="welcome-icon">🤖</div>
            <p>你好！我是AI助手，有什么问题可以问我~</p>
          </div>
          
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message', message.type]"
          >
            <div class="message-content">
              <div class="message-text">{{ message.text }}</div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
          
          <!-- 加载指示器 -->
          <div v-if="isLoading" class="message ai">
            <div class="message-content">
              <div class="message-text">
                <span class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </span>
                AI正在思考中...
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <div class="input-container">
            <input
              v-model="currentMessage"
              type="text"
              class="form-input message-input"
              placeholder="输入你的问题..."
              @keyup.enter="sendMessage"
              :disabled="isLoading"
            />
            <button 
              @click="sendMessage"
              class="btn btn-primary send-btn"
              :disabled="isLoading || !currentMessage.trim()"
            >
              <span v-if="isLoading">发送中...</span>
              <span v-else>发送</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, nextTick, onMounted } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'

export default {
  name: 'Chat',
  components: {
    Header
  },
  setup() {
    const messages = ref([])
    const currentMessage = ref('')
    const isLoading = ref(false)
    const messagesContainer = ref(null)

    // 发送消息
    const sendMessage = async () => {
      if (!currentMessage.value.trim() || isLoading.value) {
        return
      }

      const userMessage = {
        type: 'user',
        text: currentMessage.value,
        timestamp: new Date()
      }

      messages.value.push(userMessage)
      const question = currentMessage.value
      currentMessage.value = ''
      
      // 滚动到底部
      await nextTick()
      scrollToBottom()

      // 显示加载状态
      isLoading.value = true

      try {
        const response = await axios.post('http://localhost:8080/api/chat/ask', {
          question: question
        })

        if (response.data.success) {
          const aiMessage = {
            type: 'ai',
            text: response.data.data.answer,
            timestamp: new Date()
          }
          messages.value.push(aiMessage)
        } else {
          const errorMessage = {
            type: 'ai',
            text: response.data.message || '抱歉，我现在无法回答您的问题',
            timestamp: new Date()
          }
          messages.value.push(errorMessage)
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        const errorMessage = {
          type: 'ai',
          text: '抱歉，服务暂时不可用，请稍后重试',
          timestamp: new Date()
        }
        messages.value.push(errorMessage)
      } finally {
        isLoading.value = false
        await nextTick()
        scrollToBottom()
      }
    }

    // 滚动到底部
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 格式化时间
    const formatTime = (date) => {
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    onMounted(() => {
      // 组件挂载后聚焦输入框
      nextTick(() => {
        const input = document.querySelector('.message-input')
        if (input) {
          input.focus()
        }
      })
    })

    return {
      messages,
      currentMessage,
      isLoading,
      messagesContainer,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.chat-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: 600px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem;
  text-align: center;
}

.chat-header h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
}

.chat-subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  background: #f8fafc;
}

.chat-welcome {
  text-align: center;
  padding: 2rem;
  color: #6b7280;
}

.welcome-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.message {
  margin-bottom: 1rem;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.ai {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 0.75rem 1rem;
  border-radius: 1rem;
  position: relative;
}

.message.user .message-content {
  background: #3b82f6;
  color: white;
  border-bottom-right-radius: 0.25rem;
}

.message.ai .message-content {
  background: white;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 0.25rem;
}

.message-text {
  margin-bottom: 0.25rem;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.7;
  text-align: right;
}

.message.ai .message-time {
  text-align: left;
}

.typing-indicator {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  margin-right: 0.5rem;
}

.typing-indicator span {
  width: 0.5rem;
  height: 0.5rem;
  background: #6b7280;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  padding: 1rem;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.input-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.message-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 1.5rem;
  outline: none;
  font-size: 0.9rem;
}

.message-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.send-btn {
  border-radius: 1.5rem;
  padding: 0.75rem 1.5rem;
  white-space: nowrap;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>