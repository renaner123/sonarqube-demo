import { defineStore } from 'pinia'
import { ref } from 'vue'
import { taskService } from '../services/taskService'

export const useTaskStore = defineStore('task', () => {
  const tasks = ref([])

  async function fetchTasks() {
    const response = await taskService.getAll()
    tasks.value = response.data
  }

  async function createTask(taskData) {
    const response = await taskService.create(taskData)
    tasks.value.push(response.data)
    return response.data
  }

  async function updateTaskStatus(id, status) {
    await taskService.updateStatus(id, status)
    const task = tasks.value.find((t) => t.id === id)
    if (task) {
      task.status = status
    }
  }

  async function deleteTask(id) {
    await taskService.delete(id)
    tasks.value = tasks.value.filter((t) => t.id !== id)
  }

  return { tasks, fetchTasks, createTask, updateTaskStatus, deleteTask }
})
