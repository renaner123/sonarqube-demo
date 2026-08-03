package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.Category;
import com.demo.taskmanager.domain.entity.Task;
import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.enums.TaskPriority;
import com.demo.taskmanager.domain.enums.TaskStatus;
import com.demo.taskmanager.domain.repository.CategoryRepository;
import com.demo.taskmanager.domain.repository.TaskCommentRepository;
import com.demo.taskmanager.domain.repository.TaskRepository;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.TaskRequest;
import com.demo.taskmanager.dto.TaskResponse;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TaskCommentRepository taskCommentRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("Alice").email("alice@test.com").passwordHash("hash").build();
        task = Task.builder()
                .id(1L).title("Test Task").status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM).user(user).build();
    }

    @Test
    void findAllByUser_shouldReturnTaskList() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.findAllByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    void findById_shouldReturnTask_whenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse result = taskService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldCreateTask_whenValid() {
        TaskRequest request = TaskRequest.builder().title("New Task").build();
        Task saved = Task.builder()
                .id(2L).title("New Task").status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM).user(user).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.TODO)).thenReturn(Collections.emptyList());
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse result = taskService.create(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Task");
    }

    @Test
    void create_shouldThrow_whenCategoryNotFound() {
        TaskRequest request = TaskRequest.builder().title("Task").categoryId(99L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenCategoryBelongsToDifferentUser() {
        User other = User.builder().id(99L).name("Bob").email("bob@test.com").passwordHash("h").build();
        Category otherCategory = Category.builder().id(1L).name("Work").color("#fff").user(other).build();
        TaskRequest request = TaskRequest.builder().title("Task").categoryId(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> taskService.create(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void create_shouldThrow_whenOpenTaskLimitExceeded() {
        TaskRequest request = TaskRequest.builder().title("Task").build();

        List<Task> todoTasks = IntStream.range(0, 6)
                .mapToObj(i -> Task.builder().id((long) i).title("T" + i)
                        .status(TaskStatus.TODO).priority(TaskPriority.LOW).user(user).build())
                .toList();
        List<Task> inProgressTasks = IntStream.range(0, 5)
                .mapToObj(i -> Task.builder().id((long) (i + 10)).title("IP" + i)
                        .status(TaskStatus.IN_PROGRESS).priority(TaskPriority.LOW).user(user).build())
                .toList();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.TODO)).thenReturn(todoTasks);
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(inProgressTasks);

        assertThatThrownBy(() -> taskService.create(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void create_shouldThrow_whenDueDateIsInPast() {
        TaskRequest request = TaskRequest.builder()
                .title("Task")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.TODO)).thenReturn(Collections.emptyList());
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> taskService.create(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("past");
    }

    @Test
    void update_shouldUpdateTask_whenBelongsToUser() {
        TaskRequest request = TaskRequest.builder().title("Updated Title").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse result = taskService.update(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_shouldThrow_whenTaskBelongsToDifferentUser() {
        User other = User.builder().id(99L).name("Bob").email("bob@test.com").passwordHash("h").build();
        Task otherTask = Task.builder().id(1L).title("Task").status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM).user(other).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherTask));

        assertThatThrownBy(() -> taskService.update(1L, 1L, TaskRequest.builder().title("X").build()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void delete_shouldDelete_whenBelongsToUser() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L, 1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenTaskBelongsToDifferentUser() {
        User other = User.builder().id(99L).name("Bob").email("bob@test.com").passwordHash("h").build();
        Task otherTask = Task.builder().id(1L).title("Task").status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM).user(other).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(otherTask));

        assertThatThrownBy(() -> taskService.delete(1L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void search_shouldReturnFilteredList() {
        when(taskRepository.searchByTitleUnsafe(1L, "test")).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.search(1L, "test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
    }
}
