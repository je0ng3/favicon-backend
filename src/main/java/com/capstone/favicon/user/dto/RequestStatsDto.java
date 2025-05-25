package com.capstone.favicon.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RequestStatsDto {
    private int currentMonthTotal;
    private int growthFromLastMonth;
    private int currentMonthPending;
    private int pendingGrowthFromLastMonth;
    private Map<String, Integer> last6MonthsTotals; // 예: {"2025-01": 10, "2025-02": 15, ...}
}