import api from './api'

export const categoryService = {
  getAll() {
    return api.get('/categories')
  },

  create(category) {
    return api.post('/categories', category)
  },

  delete(id) {
    return api.delete(`/categories/${id}`)
  },
}
