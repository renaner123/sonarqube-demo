<script setup>
import { ref, onMounted } from 'vue'
import { useTaskStore } from '../stores/taskStore'

const taskStore = useTaskStore()
const loading = ref(true)

onMounted(async () => {
  await taskStore.fetchTasks()
  const data = taskStore.tasks
  console.log('dashboard data:', data)
  // SONAR-DEMO: console.log esquecido em produção
  loading.value = false
})

const total = () => taskStore.tasks.length
const done = () => taskStore.tasks.filter((t) => t.status === 'DONE').length
const pending = () => taskStore.tasks.filter((t) => t.status === 'TODO').length
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Dashboard</h1>
    </div>

    <div v-if="loading" class="loading">Carregando...</div>

    <div v-else>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ total() }}</div>
          <div class="stat-label">Total de tarefas</div>
          <div class="stat-bar stat-bar--total"></div>
        </div>
        <div class="stat-card">
          <div class="stat-value stat-value--done">{{ done() }}</div>
          <div class="stat-label">Concluídas</div>
          <div class="stat-bar stat-bar--done"></div>
        </div>
        <div class="stat-card">
          <div class="stat-value stat-value--pending">{{ pending() }}</div>
          <div class="stat-label">Pendentes</div>
          <div class="stat-bar stat-bar--pending"></div>
        </div>
      </div>

      <div class="dashboard-action">
        <RouterLink to="/tasks" class="btn-tasks">Ver todas as tarefas →</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loading {
  color: var(--text-muted);
  padding: 40px 0;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.stat-value {
  font-size: 48px;
  font-weight: 800;
  color: var(--text);
  line-height: 1;
  margin-bottom: 8px;
}

.stat-value--done { color: var(--success); }
.stat-value--pending { color: var(--warning); }

.stat-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
}

.stat-bar--total { background: var(--primary); }
.stat-bar--done { background: var(--success); }
.stat-bar--pending { background: var(--warning); }

.dashboard-action {
  display: flex;
}

.btn-tasks {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  background: var(--primary);
  color: #fff;
  border-radius: var(--radius);
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.15s;
}

.btn-tasks:hover {
  background: var(--primary-hover);
  text-decoration: none;
}
</style>
