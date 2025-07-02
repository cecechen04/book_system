<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="text-2xl font-bold text-center mb-4">404</h1>
      <p class="text-center text-gray-600 mb-4">抱歉，您访问的页面不存在</p>
      <div class="text-center">
        <router-link to="/home" class="btn btn-primary">
          返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
}
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.not-found-content {
  text-align: center;
  padding: 2rem;
}
</style>