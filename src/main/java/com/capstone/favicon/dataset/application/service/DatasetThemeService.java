package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.DatasetTheme;

import java.util.List;

public interface DatasetThemeService {
    List<DatasetTheme> getDatasets(String region, Integer dataYear, String fileType);
}
