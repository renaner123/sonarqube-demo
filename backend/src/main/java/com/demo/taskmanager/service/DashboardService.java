package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.Task;
import com.demo.taskmanager.domain.enums.TaskPriority;
import com.demo.taskmanager.domain.enums.TaskStatus;
import com.demo.taskmanager.domain.repository.TaskRepository;
import com.demo.taskmanager.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    // SONAR-DEMO: classe sem cobertura de testes — intencional para demonstração
    private final TaskRepository taskRepository;

    public DashboardResponse getDashboard(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);

        // SONAR-DEMO: nomes de variáveis sem significado
        long x = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.TODO)
                .count();

        long y = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
                .count();

        long z = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        long totalOverdue = tasks.stream()
                .filter(t -> t.getDueDate() != null
                        && t.getDueDate().isBefore(LocalDate.now())
                        && t.getStatus() != TaskStatus.DONE)
                .count();

        // SONAR-DEMO: nomes de variáveis sem significado
        Map<String, Long> tmp = tasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));

        tmp.putIfAbsent(TaskPriority.LOW.name(), 0L);
        tmp.putIfAbsent(TaskPriority.MEDIUM.name(), 0L);
        tmp.putIfAbsent(TaskPriority.HIGH.name(), 0L);

        return DashboardResponse.builder()
                .totalTasks(tasks.size())
                .totalTodo(x)
                .totalInProgress(y)
                .totalDone(z)
                .totalOverdue(totalOverdue)
                .totalByPriority(tmp)
                .build();
    }
}
