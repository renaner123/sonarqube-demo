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
import com.demo.taskmanager.dto.CategoryResponse;
import com.demo.taskmanager.dto.CommentResponse;
import com.demo.taskmanager.dto.TaskDetailResponse;
import com.demo.taskmanager.dto.TaskRequest;
import com.demo.taskmanager.dto.TaskResponse;
import com.demo.taskmanager.dto.UserResponse;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskCommentRepository taskCommentRepository;

    public List<TaskResponse> findAllByUser(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public TaskDetailResponse findByIdDetail(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        CategoryResponse categoryResponse = null;
        if (task.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .color(task.getCategory().getColor())
                    .build();
        }

        List<CommentResponse> comments = taskCommentRepository.findByTaskId(id).stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .author(UserResponse.builder()
                                .id(c.getUser().getId())
                                .name(c.getUser().getName())
                                .email(c.getUser().getEmail())
                                .build())
                        .build())
                .toList();

        return TaskDetailResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .category(categoryResponse)
                .commentsCount(comments.size())
                .comments(comments)
                .build();
    }

    // SONAR-DEMO: método longo com múltiplas responsabilidades
    public TaskResponse create(Long userId, TaskRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("Task title is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

            if (!category.getUser().getId().equals(userId)) {
                throw new BusinessException("Category does not belong to the user");
            }
        }

        List<Task> openTasks = new ArrayList<>(
                taskRepository.findByUserIdAndStatus(userId, TaskStatus.TODO));
        openTasks.addAll(
                taskRepository.findByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS));

        // SONAR-DEMO: número mágico sem constante nomeada
        if (openTasks.size() > 10) {
            throw new BusinessException("Open task limit exceeded. Close some tasks before creating new ones.");
        }

        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Due date cannot be in the past");
        }

        TaskStatus status = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;
        TaskPriority priority = request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM;

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(status)
                .priority(priority)
                .dueDate(request.getDueDate())
                .user(user)
                .category(category)
                .build();

        task = taskRepository.save(task);
        log.info("Task created: {} for user: {}", task.getId(), userId);

        CategoryResponse categoryResponse = null;
        if (task.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .color(task.getCategory().getColor())
                    .build();
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .category(categoryResponse)
                .commentsCount(0)
                .build();
    }

    public TaskResponse update(Long userId, Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new BusinessException("Task does not belong to the user");
        }

        // SONAR-DEMO: lógica duplicada — mesma validação de data em create e update
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Due date cannot be in the past");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            task.setCategory(category);
        }

        return toResponse(taskRepository.save(task));
    }

    public void delete(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new BusinessException("Task does not belong to the user");
        }

        taskRepository.deleteById(taskId);
    }

    public List<TaskResponse> search(Long userId, String title) {
        return taskRepository.searchByTitleUnsafe(userId, title).stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        CategoryResponse categoryResponse = null;
        if (task.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .color(task.getCategory().getColor())
                    .build();
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .category(categoryResponse)
                .commentsCount(task.getComments() != null ? task.getComments().size() : 0)
                .build();
    }
}
