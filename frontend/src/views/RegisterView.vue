<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const router = useRouter()
const auth = useAuthStore()

const name = ref('')
const email = ref('')
const password = ref('')
const error = ref('')

async function handleRegister() {
  try {
    await auth.register({ name: name.value, email: email.value, password: password.value })
    router.push('/dashboard')
  } catch (e) {
    error.value = 'Erro ao criar conta'
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-header">
        <div class="auth-logo">TF</div>
        <h1>TaskFlow</h1>
        <p>Crie sua conta</p>
      </div>
      <form class="auth-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">Nome</label>
          <input id="name" v-model="name" type="text" placeholder="Seu nome" required />
        </div>
        <div class="form-group">
          <label for="email">Email</label>
          <input id="email" v-model="email" type="email" placeholder="seu@email.com" required />
        </div>
        <div class="form-group">
          <label for="password">Senha</label>
          <input id="password" v-model="password" type="password" placeholder="••••••••" required />
        </div>
        <p v-if="error" class="error-msg">{{ error }}</p>
        <button type="submit" class="btn-full">Criar conta</button>
      </form>
      <p class="auth-footer">
        Já tem conta? <RouterLink to="/login">Entrar</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  width: 52px;
  height: 52px;
  background: var(--primary);
  color: #fff;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  margin: 0 auto 12px;
}

.auth-header h1 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 4px;
}

.auth-header p {
  font-size: 14px;
  color: var(--text-muted);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.error-msg {
  font-size: 13px;
  color: var(--danger);
  background: rgba(239, 68, 68, 0.08);
  padding: 10px 12px;
  border-radius: var(--radius);
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.btn-full {
  width: 100%;
  padding: 11px;
  font-size: 15px;
  margin-top: 4px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--text-muted);
}
</style>
