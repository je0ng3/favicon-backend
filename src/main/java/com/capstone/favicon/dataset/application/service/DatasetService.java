package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DatasetService {
    List<Dataset> findAllDatasets();

    List<Dataset> getTop9ByDownload();

    @Transactional
    void incrementDownloadCount(Long datasetId);

    List<DatasetTheme> filterByCategory(String theme);

    long getTotalDatasetCount();

    Optional<Dataset> getDatasetDetails(Long datasetId);

    Map<String, Map<String, Object>> getThemeStats();

    List<Dataset> getDatasetsByCategory(Long datasetThemeId);

    List<Dataset> search(String text);

    Map<String, List<String>> getDatasetNameGroupByTheme();
}
