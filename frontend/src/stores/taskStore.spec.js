import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTaskStore } from './taskStore'
import { taskService } from '../services/taskService'

vi.mock('../services/taskService', () => ({
  taskService: {
    getAll: vi.fn(),
    create: vi.fn(),
    updateStatus: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('taskStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchTasks chama o service e popula o store', async () => {
    const mockTasks = [
      { id: 1, title: 'Tarefa 1', status: 'PENDING' },
      { id: 2, title: 'Tarefa 2', status: 'DONE' },
    ]
    taskService.getAll.mockResolvedValue({ data: mockTasks })

    const store = useTaskStore()
    await store.fetchTasks()

    expect(taskService.getAll).toHaveBeenCalledOnce()
    expect(store.tasks).toEqual(mockTasks)
  })

  it('createTask chama o service, adiciona na lista e retorna a task criada', async () => {
    const newTask = { id: 3, title: 'Nova tarefa', status: 'PENDING' }
    taskService.getAll.mockResolvedValue({ data: [] })
    taskService.create.mockResolvedValue({ data: newTask })

    const store = useTaskStore()
    await store.fetchTasks()
    const result = await store.createTask({ title: 'Nova tarefa' })

    expect(taskService.create).toHaveBeenCalledWith({ title: 'Nova tarefa' })
    expect(store.tasks).toContainEqual(newTask)
    expect(result).toEqual(newTask)
  })

  it('updateTaskStatus atualiza o status de uma task na lista', async () => {
    const tasks = [{ id: 1, title: 'Tarefa', status: 'PENDING' }]
    taskService.getAll.mockResolvedValue({ data: tasks })
    taskService.updateStatus.mockResolvedValue({})

    const store = useTaskStore()
    await store.fetchTasks()
    await store.updateTaskStatus(1, 'DONE')

    expect(taskService.updateStatus).toHaveBeenCalledWith(1, 'DONE')
    expect(store.tasks[0].status).toBe('DONE')
  })

  it('deleteTask remove a task da lista', async () => {
    const tasks = [
      { id: 1, title: 'Tarefa 1', status: 'PENDING' },
      { id: 2, title: 'Tarefa 2', status: 'DONE' },
    ]
    taskService.getAll.mockResolvedValue({ data: tasks })
    taskService.delete.mockResolvedValue({})

    const store = useTaskStore()
    await store.fetchTasks()
    await store.deleteTask(1)

    expect(taskService.delete).toHaveBeenCalledWith(1)
    expect(store.tasks).toHaveLength(1)
    expect(store.tasks[0].id).toBe(2)
  })
})
