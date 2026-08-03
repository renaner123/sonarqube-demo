<script setup>
defineProps({
  category: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <span class="category-badge">{{ category.name }}</span>
</template>

<style scoped>
.category-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  background: #ede9fe;
  color: #6d28d9;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
</style>
