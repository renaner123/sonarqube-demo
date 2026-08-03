package com.demo.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalTasks;
    private long totalTodo;
    private long totalInProgress;
    private long totalDone;
    private long totalOverdue;
    private Map<String, Long> totalByPriority;
}
