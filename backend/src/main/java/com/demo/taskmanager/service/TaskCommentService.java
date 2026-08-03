package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.Task;
import com.demo.taskmanager.domain.entity.TaskComment;
import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.repository.TaskCommentRepository;
import com.demo.taskmanager.domain.repository.TaskRepository;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.CommentRequest;
import com.demo.taskmanager.dto.CommentResponse;
import com.demo.taskmanager.dto.UserResponse;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCommentService {

    // SONAR-DEMO: classe sem cobertura de testes — intencional para demonstração
    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> findByTask(Long taskId) {
        return taskCommentRepository.findByTaskId(taskId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponse addComment(Long taskId, Long userId, CommentRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        TaskComment comment = TaskComment.builder()
                .task(task)
                .user(user)
                .content(request.getContent())
                .build();

        return toResponse(taskCommentRepository.save(comment));
    }

    public void deleteComment(Long commentId, Long userId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("Comment does not belong to the user");
        }

        try {
            taskCommentRepository.deleteById(commentId);
        } catch (Exception e) {
            // silently ignored
            // SONAR-DEMO: exceção genérica capturada e ignorada silenciosamente
        }
    }

    private CommentResponse toResponse(TaskComment comment) {
        UserResponse author = UserResponse.builder()
                .id(comment.getUser().getId())
                .name(comment.getUser().getName())
                .email(comment.getUser().getEmail())
                .build();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .author(author)
                .build();
    }
}
