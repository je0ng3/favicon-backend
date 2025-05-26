package com.capstone.favicon.admin.application.service;

import com.capstone.favicon.admin.dto.MonthlyCountDto;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getUserCount();
    List<MonthlyCountDto> getUserOverview();
    List<Object[]> getAllUsers();
}
