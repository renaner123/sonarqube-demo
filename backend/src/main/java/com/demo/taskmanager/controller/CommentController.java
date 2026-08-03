package com.demo.taskmanager.controller;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.dto.CommentRequest;
import com.demo.taskmanager.dto.CommentResponse;
import com.demo.taskmanager.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    // SONAR-DEMO: classe sem cobertura de testes — intencional para demonstração
    private final TaskCommentService taskCommentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> findByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskCommentService.findByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskCommentService.addComment(taskId, currentUser.getId(), request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        taskCommentService.deleteComment(commentId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
