package com.capstone.favicon.admin.controller;

import com.capstone.favicon.admin.application.service.StatisticsService;
import com.capstone.favicon.admin.dto.MonthlyCountDto;
import com.capstone.favicon.config.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/user-stats")
    public ResponseEntity<APIResponse<?>> getUserStats() {
        Map<String, Object> userStats = statisticsService.getUserCount();
        return ResponseEntity.ok().body(APIResponse.successAPI("전체 사용자 & 지난달 대비 증가추이 비율", userStats));
    }

    @GetMapping("/user-overview")
    public ResponseEntity<APIResponse<?>> getUserOverview() {
        List<MonthlyCountDto> userOverview = statisticsService.getUserOverview();
        return ResponseEntity.ok().body(APIResponse.successAPI("사용자 개요", userOverview));
    }

    @GetMapping("/all-user")
    public ResponseEntity<APIResponse<?>> getAllUser() {
        List<Object[]> users = statisticsService.getAllUsers();
        return ResponseEntity.ok().body(APIResponse.successAPI("전체 사용자", users));
    }
}
