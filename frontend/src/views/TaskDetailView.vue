<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { taskService } from '../services/taskService'

const route = useRoute()
const router = useRouter()
const task = ref(null)
const loading = ref(true)

onMounted(async () => {
  const response = await taskService.getById(route.params.id)
  task.value = response.data
  loading.value = false
})

async function handleDelete() {
  await taskService.delete(route.params.id)
  router.push('/tasks')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <RouterLink to="/tasks" class="back-link">← Voltar às tarefas</RouterLink>
    </div>

    <div v-if="loading" class="loading">Carregando...</div>

    <div v-else-if="task" class="detail-card card">
      <h1 class="task-title">{{ task.title }}</h1>

      <p v-if="task.description" class="task-description">{{ task.description }}</p>

      <div class="task-meta">
        <div class="meta-item">
          <span class="meta-label">Status</span>
          <span class="badge" :class="'badge--' + task.status.toLowerCase()">
            {{ task.status === 'TODO' ? 'Pendente' : task.status === 'IN_PROGRESS' ? 'Em andamento' : 'Concluída' }}
          </span>
        </div>

        <div class="meta-item">
          <span class="meta-label">Prioridade</span>
          <span class="badge" :class="'badge--priority-' + task.priority.toLowerCase()">
            {{ task.priority === 'LOW' ? 'Baixa' : task.priority === 'MEDIUM' ? 'Média' : 'Alta' }}
          </span>
        </div>

        <div class="meta-item">
          <span class="meta-label">Categoria</span>
          <!-- SONAR-DEMO: acesso sem verificação — se category for null lança erro -->
          <span class="meta-value">{{ task.category.name }}</span>
        </div>

        <div v-if="task.dueDate" class="meta-item">
          <span class="meta-label">Vencimento</span>
          <span class="meta-value">{{ task.dueDate }}</span>
        </div>
      </div>

      <div class="detail-footer">
        <button class="btn-danger" @click="handleDelete">Excluir tarefa</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-link {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  text-decoration: none;
  transition: color 0.15s;
}

.back-link:hover {
  color: var(--primary);
  text-decoration: none;
}

.loading {
  color: var(--text-muted);
  padding: 40px 0;
  text-align: center;
}

.detail-card {
  max-width: 680px;
}

.task-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 12px;
}

.task-description {
  font-size: 15px;
  color: var(--text-muted);
  margin-bottom: 24px;
  line-height: 1.7;
}

.task-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 32px;
  padding: 20px;
  background: var(--bg);
  border-radius: var(--radius);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.meta-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  width: fit-content;
}

.badge--todo { background: #f1f5f9; color: var(--text-muted); }
.badge--in_progress { background: #dbeafe; color: #1d4ed8; }
.badge--done { background: #dcfce7; color: var(--success); }

.badge--priority-low { background: #dcfce7; color: var(--success); }
.badge--priority-medium { background: #fef3c7; color: var(--warning); }
.badge--priority-high { background: #fee2e2; color: var(--danger); }

.detail-footer {
  padding-top: 20px;
  border-top: 1px solid var(--border);
}
</style>
