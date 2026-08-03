package com.demo.taskmanager.controller;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.dto.DashboardResponse;
import com.demo.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    // SONAR-DEMO: classe sem cobertura de testes — intencional para demonstração
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(dashboardService.getDashboard(currentUser.getId()));
    }
}
