package com.demo.taskmanager.controller;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.dto.TaskDetailResponse;
import com.demo.taskmanager.dto.TaskRequest;
import com.demo.taskmanager.dto.TaskResponse;
import com.demo.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    // SONAR-DEMO: classe sem cobertura de testes — intencional para demonstração
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.findAllByUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findByIdDetail(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(currentUser.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(currentUser.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        taskService.delete(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> search(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("q") String q) {
        return ResponseEntity.ok(taskService.search(currentUser.getId(), q));
    }
}
