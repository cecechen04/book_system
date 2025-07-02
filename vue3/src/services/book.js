import api from './api'

export const bookService = {
  // 获取所有图书
  async getAllBooks() {
    const response = await api.get('/books')
    return response
  },

  // 根据ID获取图书详情
  async getBookById(id) {
    const response = await api.get(`/books/${id}`)
    return response
  },

  // 根据分类ID获取图书
  async getBooksByCategory(categoryId) {
    const response = await api.get(`/books/category/${categoryId}`)
    return response
  },

  // 搜索图书
  async searchBooks(keyword) {
    const response = await api.get(`/books/search?keyword=${encodeURIComponent(keyword)}`)
    return response
  },

  // 添加图书
  async addBook(book) {
    const response = await api.post('/books', book)
    return response
  },

  // 更新图书
  async updateBook(id, book) {
    const response = await api.put(`/books/${id}`, book)
    return response
  },

  // 删除图书
  async deleteBook(id) {
    const response = await api.delete(`/books/${id}`)
    return response
  }
}