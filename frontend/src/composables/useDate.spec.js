import { describe, it, expect } from 'vitest'
import { useDate } from './useDate'

describe('useDate', () => {
  describe('formatDate', () => {
    it('formata data válida', () => {
      const { formatDate } = useDate()
      const result = formatDate('2024-06-15')
      expect(result).toBe('14/06/2024')
    })

    it('retorna string vazia para null', () => {
      const { formatDate } = useDate()
      expect(formatDate(null)).toBe('')
    })

    it('retorna string vazia para data inválida', () => {
      const { formatDate } = useDate()
      expect(formatDate('nao-e-data')).toBe('')
    })
  })

  describe('isOverdue', () => {
    it('retorna true para data no passado', () => {
      const { isOverdue } = useDate()
      expect(isOverdue('2000-01-01')).toBe(true)
    })

    it('retorna false para data no futuro', () => {
      const { isOverdue } = useDate()
      expect(isOverdue('2099-12-31')).toBe(false)
    })

    it('retorna false para null', () => {
      const { isOverdue } = useDate()
      expect(isOverdue(null)).toBe(false)
    })
  })
})
