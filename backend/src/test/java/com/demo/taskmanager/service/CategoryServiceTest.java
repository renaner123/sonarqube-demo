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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskRepository taskRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Alice").email("alice@test.com").passwordHash("hash").build();
        category = Category.builder().id(1L).name("Work").color("#6366f1").user(user).build();
    }

    @Test
    void findAllByUser_shouldReturnUserCategories() {
        when(categoryRepository.findByUserId(1L)).thenReturn(List.of(category));

        List<CategoryResponse> result = categoryService.findAllByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Work");
    }

    @Test
    void findById_shouldReturnCategory_whenFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Work");
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldCreateCategory_whenValid() {
        CategoryRequest request = new CategoryRequest("Work", "#6366f1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByUserIdAndName(1L, "Work")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse result = categoryService.create(1L, request);

        assertThat(result.getName()).isEqualTo("Work");
        assertThat(result.getColor()).isEqualTo("#6366f1");
    }

    @Test
    void create_shouldThrow_whenNameAlreadyExistsForUser() {
        CategoryRequest request = new CategoryRequest("Work", "#6366f1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByUserIdAndName(1L, "Work")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void update_shouldUpdateCategory_whenFound() {
        CategoryRequest request = new CategoryRequest("Personal", "#10b981");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse result = categoryService.update(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void delete_shouldDelete_whenNoLinkedTasks() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenCategoryHasLinkedTasks() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("tasks");
    }
}
