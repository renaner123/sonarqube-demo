export function useDate() {
  function formatDate(date) {
    if (!date) return ''
    const d = new Date(date)
    if (isNaN(d.getTime())) return ''
    return d.toLocaleDateString('pt-BR')
  }

  function isOverdue(date) {
    if (!date) return false
    return new Date(date) < new Date()
  }

  return { formatDate, isOverdue }
}
