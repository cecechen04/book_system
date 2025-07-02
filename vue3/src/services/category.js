import api from './api'

export const categoryService = {
  // 获取所有一级分类
  async getLevel1Categories() {
    const response = await api.get('/categories/level1')
    return response
  },

  // 获取所有二级分类
  async getLevel2Categories() {
    const response = await api.get('/categories/level2')
    return response
  },

  // 根据父分类ID获取二级分类
  async getLevel2ByParent(parentId) {
    const response = await api.get(`/categories/level2/parent/${parentId}`)
    return response
  },

  // 添加一级分类
  async addLevel1Category(name) {
    const response = await api.post('/categories/level1', { name })
    return response
  },

  // 添加二级分类
  async addLevel2Category(name, parentId) {
    const response = await api.post('/categories/level2', { name, parentId })
    return response
  },

  // 更新一级分类
  async updateLevel1Category(id, name) {
    const response = await api.put(`/categories/level1/${id}`, { name })
    return response
  },

  // 更新二级分类
  async updateLevel2Category(id, name, parentId) {
    const response = await api.put(`/categories/level2/${id}`, { name, parentId })
    return response
  },

  // 删除一级分类
  async deleteLevel1Category(id) {
    const response = await api.delete(`/categories/level1/${id}`)
    return response
  },

  // 删除二级分类
  async deleteLevel2Category(id) {
    const response = await api.delete(`/categories/level2/${id}`)
    return response
  }
}