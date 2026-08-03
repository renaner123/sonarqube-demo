<script setup>
import { ref, onMounted } from 'vue'
import { categoryService } from '../services/categoryService'

const categories = ref([])
const newCategoryName = ref('')
const loading = ref(true)

onMounted(async () => {
  const response = await categoryService.getAll()
  categories.value = response.data
  loading.value = false
})

async function handleCreate() {
  if (!newCategoryName.value.trim()) return
  const response = await categoryService.create({ name: newCategoryName.value })
  categories.value.push(response.data)
  newCategoryName.value = ''
}

async function handleDelete(id) {
  await categoryService.delete(id)
  categories.value = categories.value.filter((c) => c.id !== id)
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Categorias</h1>
    </div>

    <div class="add-form card">
      <form class="add-form-inner" @submit.prevent="handleCreate">
        <div class="form-group">
          <label for="category-name">Nova categoria</label>
          <input
            id="category-name"
            v-model="newCategoryName"
            type="text"
            placeholder="Ex: Trabalho, Pessoal..."
            required
          />
        </div>
        <button type="submit">Adicionar</button>
      </form>
    </div>

    <div v-if="loading" class="loading">Carregando...</div>

    <div v-else-if="categories.length === 0" class="empty-state">
      <p>Nenhuma categoria cadastrada.</p>
    </div>

    <ul v-else class="category-list">
      <li v-for="cat in categories" :key="cat.id" class="category-item card">
        <span class="category-name">{{ cat.name }}</span>
        <button class="btn-danger btn-sm" @click="handleDelete(cat.id)">Remover</button>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.add-form {
  margin-bottom: 24px;
}

.add-form-inner {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.loading {
  color: var(--text-muted);
  padding: 40px 0;
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: var(--text-muted);
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1.5px dashed var(--border);
}

.category-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
}

.category-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--text);
}
</style>
