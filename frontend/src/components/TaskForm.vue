<script setup>
import { ref } from 'vue'

const emit = defineEmits(['submit'])

const form = ref({
  title: '',
  description: '',
  priority: 'MEDIUM',
  dueDate: '',
  categoryId: null,
})

function handleSubmit() {
  emit('submit', { ...form.value })
}
</script>

<template>
  <form class="task-form card" @submit.prevent="handleSubmit">
    <h2 class="form-title">Nova tarefa</h2>
    <div class="form-grid">
      <div class="form-group form-group--full">
        <label for="task-title">Título</label>
        <input id="task-title" v-model="form.title" type="text" placeholder="Título da tarefa" required />
      </div>
      <div class="form-group form-group--full">
        <label for="task-desc">Descrição</label>
        <textarea id="task-desc" v-model="form.description" rows="3" placeholder="Descreva a tarefa..."></textarea>
      </div>
      <div class="form-group">
        <label for="task-priority">Prioridade</label>
        <select id="task-priority" v-model="form.priority">
          <option value="LOW">Baixa</option>
          <option value="MEDIUM">Média</option>
          <option value="HIGH">Alta</option>
        </select>
      </div>
      <div class="form-group">
        <label for="task-due">Data de vencimento</label>
        <input id="task-due" v-model="form.dueDate" type="date" />
      </div>
    </div>
    <div class="form-footer">
      <button type="submit">Salvar tarefa</button>
    </div>
  </form>
</template>

<style scoped>
.task-form {
  margin-bottom: 20px;
}

.form-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group--full {
  grid-column: 1 / -1;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
