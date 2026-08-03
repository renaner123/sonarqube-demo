<script setup>
import CategoryBadge from './CategoryBadge.vue'

defineProps({
  task: {
    type: Object,
    required: true,
  },
})

const statusLabel = { TODO: 'Pendente', IN_PROGRESS: 'Em andamento', DONE: 'Concluída' }
const priorityLabel = { LOW: 'Baixa', MEDIUM: 'Média', HIGH: 'Alta' }
</script>

<template>
  <div class="task-card">
    <h3 class="task-title">{{ task.title }}</h3>
    <p v-if="task.description" class="task-desc">{{ task.description }}</p>
    <div class="task-meta">
      <span class="badge" :class="'badge--' + task.status.toLowerCase()">
        {{ statusLabel[task.status] }}
      </span>
      <span class="badge" :class="'badge--priority-' + task.priority.toLowerCase()">
        {{ priorityLabel[task.priority] }}
      </span>
      <CategoryBadge v-if="task.category" :category="task.category" />
    </div>
  </div>
</template>

<style scoped>
.task-card {
  padding-bottom: 4px;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.task-desc {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 10px;
  line-height: 1.5;
}

.task-meta {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: center;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.badge--todo { background: #f1f5f9; color: var(--text-muted); }
.badge--in_progress { background: #dbeafe; color: #1d4ed8; }
.badge--done { background: #dcfce7; color: #15803d; }

.badge--priority-low { background: #dcfce7; color: #15803d; }
.badge--priority-medium { background: #fef3c7; color: #92400e; }
.badge--priority-high { background: #fee2e2; color: #b91c1c; }
</style>
