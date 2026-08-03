<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/authStore'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <nav v-if="auth.isAuthenticated" class="navbar">
    <div class="nav-inner">
      <div class="nav-brand">TaskFlow</div>
      <div class="nav-links">
        <RouterLink to="/dashboard" active-class="active">Dashboard</RouterLink>
        <RouterLink to="/tasks" active-class="active">Tarefas</RouterLink>
        <RouterLink to="/categories" active-class="active">Categorias</RouterLink>
      </div>
      <button class="btn-ghost btn-sm" @click="logout">Sair</button>
    </div>
  </nav>
  <main :class="{ 'with-nav': auth.isAuthenticated }">
    <RouterView />
  </main>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1.5px solid var(--border);
  height: var(--nav-height);
  display: flex;
  align-items: center;
}

.nav-inner {
  max-width: 920px;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-brand {
  font-size: 17px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: -0.02em;
}

.nav-links {
  display: flex;
  gap: 2px;
  flex: 1;
}

.nav-links a {
  padding: 6px 12px;
  border-radius: var(--radius);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.15s, background 0.15s;
  text-decoration: none;
}

.nav-links a:hover,
.nav-links a.active {
  color: var(--primary);
  background: rgba(79, 70, 229, 0.08);
}

.with-nav {
  min-height: calc(100vh - var(--nav-height));
}
</style>
