package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.DatasetTheme;

import java.util.List;

public interface DatasetThemeService {
    List<DatasetTheme> getFilteredDatasets(String region, Integer dataYear, String fileType);

    List<DatasetTheme> getDatasetsByRegionAndDataYear(String region, Integer dataYear);

    List<DatasetTheme> getDatasetsByRegionAndFileType(String region, String fileType);

    List<DatasetTheme> getDatasetsByDataYearAndFileType(Integer dataYear, String fileType);

    List<DatasetTheme> getDatasetsByRegion(String region);

    List<DatasetTheme> getDatasetsByDataYear(Integer dataYear);

    List<DatasetTheme> getDatasetsByFileType(String fileType);

    List<DatasetTheme> getAllDatasets();
}
