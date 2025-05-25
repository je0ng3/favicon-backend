package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RequestStatsDto {
    private final int currentMonthTotal;
    private final int growthFromLastMonth;
    private final int currentMonthPending;
    private final int pendingGrowthFromLastMonth;
    private final Map<String, Integer> last6MonthsTotals;

    public RequestStatsDto(int currentMonthTotal, int growthFromLastMonth,
                           int currentMonthPending, int pendingGrowthFromLastMonth,
                           Map<String, Integer> last6MonthsTotals) {
        this.currentMonthTotal = currentMonthTotal;
        this.growthFromLastMonth = growthFromLastMonth;
        this.currentMonthPending = currentMonthPending;
        this.pendingGrowthFromLastMonth = pendingGrowthFromLastMonth;
        this.last6MonthsTotals = last6MonthsTotals;
    }

}