// SONAR-DEMO: store sem cobertura de testes — intencional para demonstração
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/authService'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(credentials) {
    const response = await authService.login(credentials)
    token.value = response.data.token
    user.value = { name: response.data.name, email: response.data.email }
    localStorage.setItem('token', token.value)
  }

  async function register(userData) {
    const response = await authService.register(userData)
    token.value = response.data.token
    user.value = { name: response.data.name, email: response.data.email }
    localStorage.setItem('token', token.value)
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isAuthenticated, login, register, logout }
})
