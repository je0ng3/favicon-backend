package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.Trend;

import java.time.LocalDate;
import java.util.List;

public interface TrendService {

    List<Trend> getTrendsByDate(LocalDate date);

    List<Trend> getTrendsByDatasetId(Long datasetId, LocalDate startDate, LocalDate endDate);

    long getCurrentRank(Long datasetId);
}
