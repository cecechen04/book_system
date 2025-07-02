<template>
  <div v-if="showDebug" class="debug-info">
    <h4>调试信息:</h4>
    <p>认证状态: {{ isAuthenticated }}</p>
    <p>用户信息: {{ JSON.stringify(user) }}</p>
    <p>用户角色: {{ user?.role }}</p>
    <p>是否管理员: {{ isAdmin }}</p>
    <button @click="toggleDebug" class="btn btn-sm btn-secondary">隐藏调试</button>
  </div>
  <button v-else @click="toggleDebug" class="debug-toggle btn btn-sm btn-secondary">
    显示调试信息
  </button>
</template>

<script>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'DebugInfo',
  setup() {
    const authStore = useAuthStore()
    const showDebug = ref(false)
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    const user = computed(() => authStore.user)
    
    const toggleDebug = () => {
      showDebug.value = !showDebug.value
    }
    
    return {
      showDebug,
      isAuthenticated,
      isAdmin,
      user,
      toggleDebug
    }
  }
}
</script>

<style scoped>
.debug-info {
  position: fixed;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 1rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  z-index: 9999;
  max-width: 300px;
}

.debug-toggle {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 9999;
}
</style>