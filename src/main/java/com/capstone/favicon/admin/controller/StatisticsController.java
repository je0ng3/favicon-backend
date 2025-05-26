package com.capstone.favicon.admin.controller;

import com.capstone.favicon.admin.application.service.StatisticsService;
import com.capstone.favicon.admin.dto.MonthlyCountDto;
import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/user-stats")
    public ResponseEntity<APIResponse<?>> getUserStats() {
        try {
            Map<String, Object> userStats = statisticsService.getUserCount();
            return ResponseEntity.ok().body(APIResponse.successAPI("전체 사용자 & 지난달 대비 증가추이 비율", userStats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/user-overview")
    public ResponseEntity<APIResponse<?>> getUserOverview() {
        try {
            List<MonthlyCountDto> userOverview = statisticsService.getUserOverview();
            return ResponseEntity.ok().body(APIResponse.successAPI("사용자 개요", userOverview));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<APIResponse<?>> getAllUser() {
        try {
            List<Object[]> users = statisticsService.getAllUsers();
            return ResponseEntity.ok().body(APIResponse.successAPI("전체 사용자", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }
}
