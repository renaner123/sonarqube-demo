import api from './api'

export const taskService = {
  getAll() {
    return api.get('/tasks')
  },

  getById(id) {
    return api.get(`/tasks/${id}`)
  },

  create(task) {
    return api.post('/tasks', task)
  },

  update(id, task) {
    return api.put(`/tasks/${id}`, task)
  },

  updateStatus(id, status) {
    return api.patch(`/tasks/${id}/status`, { status })
  },

  delete(id) {
    return api.delete(`/tasks/${id}`)
  },
}
