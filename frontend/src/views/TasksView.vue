<script setup>
// SONAR-DEMO: componente com múltiplas responsabilidades, deveria ser separado em composables
import { ref, computed, onMounted } from 'vue'
import { useTaskStore } from '../stores/taskStore'
import { categoryService } from '../services/categoryService'
import TaskCard from '../components/TaskCard.vue'
import TaskForm from '../components/TaskForm.vue'

const taskStore = useTaskStore()
const loading = ref(true)
const showForm = ref(false)
const categories = ref([])

const filterStatus = ref('')
const filterPriority = ref('')
const filterCategory = ref('')
const sortBy = ref('createdAt')
const sortOrder = ref('desc')

const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 }

onMounted(async () => {
  await taskStore.fetchTasks()
  const response = await categoryService.getAll()
  categories.value = response.data
  console.log('tasks carregadas:', tasks.value)
  // SONAR-DEMO: console.log esquecido em produção
  loading.value = false
})

const tasks = computed(() => taskStore.tasks)

const filteredTasks = computed(() => {
  let result = tasks.value

  if (filterStatus.value) {
    result = result.filter((t) => t.status === filterStatus.value)
  }

  if (filterPriority.value) {
    result = result.filter((t) => t.priority === filterPriority.value)
  }

  if (filterCategory.value) {
    result = result.filter(
      (t) => t.category && String(t.category.id) === filterCategory.value
    )
  }

  return result
})

const sortedTasks = computed(() => {
  const result = [...filteredTasks.value]
  result.sort((a, b) => {
    let valA = sortBy.value === 'priority' ? (priorityOrder[a.priority] ?? 0) : a[sortBy.value]
    let valB = sortBy.value === 'priority' ? (priorityOrder[b.priority] ?? 0) : b[sortBy.value]
    if (valA == null) return 1
    if (valB == null) return -1
    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1
    return 0
  })
  return result
})

// SONAR-DEMO: função de formatação duplicada — já existe em useDate.js
function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  if (Number.isNaN(d.getTime())) return ''
  return d.toLocaleDateString('pt-BR')
}

// SONAR-DEMO: lógica de overdue duplicada — já existe em useDate.js
function isOverdue(date) {
  if (!date) return false
  return new Date(date) < new Date()
}

async function handleCreate(taskData) {
  await taskStore.createTask(taskData)
  showForm.value = false
}

async function handleStatusChange(task, status) {
  await taskStore.updateTaskStatus(task.id, status)
}

async function handleDelete(id) {
  await taskStore.deleteTask(id)
}

function clearFilters() {
  filterStatus.value = ''
  filterPriority.value = ''
  filterCategory.value = ''
  sortBy.value = 'createdAt'
  sortOrder.value = 'desc'
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Tarefas</h1>
      <button @click="showForm = !showForm" :class="showForm ? 'btn-secondary' : ''">
        {{ showForm ? 'Cancelar' : '+ Nova tarefa' }}
      </button>
    </div>

    <TaskForm v-if="showForm" @submit="handleCreate" class="task-form-wrapper" />

    <div class="filters card">
      <select v-model="filterStatus">
        <option value="">Todos os status</option>
        <option value="TODO">Pendente</option>
        <option value="IN_PROGRESS">Em andamento</option>
        <option value="DONE">Concluída</option>
      </select>

      <select v-model="filterPriority">
        <option value="">Todas as prioridades</option>
        <option value="LOW">Baixa</option>
        <option value="MEDIUM">Média</option>
        <option value="HIGH">Alta</option>
      </select>

      <select v-model="filterCategory">
        <option value="">Todas as categorias</option>
        <option v-for="cat in categories" :key="cat.id" :value="String(cat.id)">
          {{ cat.name }}
        </option>
      </select>

      <select v-model="sortBy">
        <option value="createdAt">Ordenar por criação</option>
        <option value="dueDate">Ordenar por vencimento</option>
        <option value="priority">Ordenar por prioridade</option>
        <option value="title">Ordenar por título</option>
      </select>

      <select v-model="sortOrder">
        <option value="asc">Crescente</option>
        <option value="desc">Decrescente</option>
      </select>

      <button class="btn-secondary btn-sm" @click="clearFilters">Limpar filtros</button>
    </div>

    <div v-if="loading" class="loading">Carregando...</div>

    <div v-else>
      <p class="result-count">{{ sortedTasks.length }} tarefa(s) encontrada(s)</p>

      <div
        v-for="task in sortedTasks"
        :key="task.id"
        class="task-item"
        :class="{ overdue: isOverdue(task.dueDate) && task.status !== 'DONE' }"
      >
        <TaskCard :task="task" />

        <div class="task-actions">
          <span
            v-if="isOverdue(task.dueDate) && task.status !== 'DONE'"
            class="overdue-badge"
          >
            Atrasada
          </span>

          <span v-if="task.dueDate" class="due-date-text">
            Vence em: {{ formatDate(task.dueDate) }}
          </span>

          <select
            class="status-select"
            :value="task.status"
            @change="handleStatusChange(task, $event.target.value)"
          >
            <option value="TODO">Pendente</option>
            <option value="IN_PROGRESS">Em andamento</option>
            <option value="DONE">Concluída</option>
          </select>

          <RouterLink :to="`/tasks/${task.id}`" class="btn-detail">Detalhes</RouterLink>
          <button class="btn-danger btn-sm" @click="handleDelete(task.id)">Excluir</button>
        </div>
      </div>

      <div v-if="sortedTasks.length === 0" class="empty-state">
        <p>Nenhuma tarefa encontrada.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.task-form-wrapper {
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
  padding: 16px;
}

.filters select {
  width: auto;
  flex: 1;
  min-width: 140px;
}

.loading {
  color: var(--text-muted);
  padding: 40px 0;
  text-align: center;
}

.result-count {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.task-item {
  background: var(--card-bg);
  border: 1.5px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 16px;
  margin-bottom: 10px;
  transition: border-color 0.15s, box-shadow 0.15s;
  box-shadow: var(--shadow);
}

.task-item:hover {
  box-shadow: var(--shadow-md);
}

.task-item.overdue {
  border-color: #fca5a5;
  background: #fff5f5;
}

.task-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
  flex-wrap: wrap;
}

.overdue-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  background: #fee2e2;
  color: var(--danger);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.due-date-text {
  font-size: 13px;
  color: var(--text-muted);
  margin-right: auto;
}

.status-select {
  width: auto;
  font-size: 13px;
  padding: 5px 10px;
}

.btn-detail {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  background: #f1f5f9;
  color: var(--text);
  border-radius: var(--radius);
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  border: 1.5px solid var(--border);
  transition: background 0.15s;
}

.btn-detail:hover {
  background: #e2e8f0;
  text-decoration: none;
}

.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: var(--text-muted);
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1.5px dashed var(--border);
}
</style>
