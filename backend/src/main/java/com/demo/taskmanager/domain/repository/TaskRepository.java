package com.demo.taskmanager.domain.repository;

import com.demo.taskmanager.domain.entity.Task;
import com.demo.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    List<Task> findByUserIdAndCategoryId(Long userId, Long categoryId);

    // SONAR-DEMO: query construída de forma insegura, vulnerável a injeção
    @Query("SELECT t FROM Task t WHERE t.user.id = " + "?1 AND t.title LIKE '%" + "?2" + "%'")
    List<Task> searchByTitleUnsafe(Long userId, String title);

    boolean existsByCategoryId(Long categoryId);
}
