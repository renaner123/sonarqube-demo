package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.Category;
import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.repository.CategoryRepository;
import com.demo.taskmanager.domain.repository.TaskRepository;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.CategoryRequest;
import com.demo.taskmanager.dto.CategoryResponse;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public List<CategoryResponse> findAllByUser(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return toResponse(category);
    }

    public CategoryResponse create(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new BusinessException("Category '" + request.getName() + "' already exists for this user");
        }

        Category category = Category.builder()
                .name(request.getName())
                .color(request.getColor() != null ? request.getColor() : "#6366f1")
                .user(user)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setName(request.getName());
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }

        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        if (taskRepository.existsByCategoryId(id)) {
            throw new BusinessException("Cannot delete category with linked tasks");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .build();
    }
}
