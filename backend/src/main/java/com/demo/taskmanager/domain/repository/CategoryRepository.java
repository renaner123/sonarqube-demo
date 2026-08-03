package com.demo.taskmanager.domain.repository;

import com.demo.taskmanager.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    boolean existsByUserIdAndName(Long userId, String name);
}
