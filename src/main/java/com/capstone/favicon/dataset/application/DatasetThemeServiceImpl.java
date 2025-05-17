package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.DatasetThemeService;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetThemeServiceImpl implements DatasetThemeService {

    private final DatasetThemeRepository datasetThemeRepository;

    @Autowired
    public DatasetThemeServiceImpl(DatasetThemeRepository datasetThemeRepository) {
        this.datasetThemeRepository = datasetThemeRepository;
    }

    @Override
    public List<DatasetTheme> getDatasets (String region, Integer dataYear, String fileType) {
        if (region != null && dataYear != null && fileType != null) {
            return getFilteredDatasets(region, dataYear, fileType);
        } else if (region != null && dataYear != null) {
            return getDatasetsByRegionAndDataYear(region, dataYear);
        } else if (region != null && fileType != null) {
            return getDatasetsByRegionAndFileType(region, fileType);
        } else if (dataYear != null && fileType != null) {
            return getDatasetsByDataYearAndFileType(dataYear, fileType);
        } else if (region != null) {
            return getDatasetsByRegion(region);
        } else if (dataYear != null) {
            return getDatasetsByDataYear(dataYear);
        } else if (fileType != null) {
            return getDatasetsByFileType(fileType);
        } else {
            return getAllDatasets();
        }
    }

    private List<DatasetTheme> getFilteredDatasets(String region, Integer dataYear, String fileType) {
        return datasetThemeRepository.findByRegionAndDataYearAndFileType(region, dataYear, fileType);
    }

    private List<DatasetTheme> getDatasetsByRegionAndDataYear(String region, Integer dataYear) {
        return datasetThemeRepository.findByRegionAndDataYear(region, dataYear);
    }

    private List<DatasetTheme> getDatasetsByRegionAndFileType(String region, String fileType) {
        return datasetThemeRepository.findByRegionAndFileType(region, fileType);
    }

    private List<DatasetTheme> getDatasetsByDataYearAndFileType(Integer dataYear, String fileType) {
        return datasetThemeRepository.findByDataYearAndFileType(dataYear, fileType);
    }

    private List<DatasetTheme> getDatasetsByRegion(String region) {
        return datasetThemeRepository.findByRegion(region);
    }

    private List<DatasetTheme> getDatasetsByDataYear(Integer dataYear) {
        return datasetThemeRepository.findByDataYear(dataYear);
    }

    private List<DatasetTheme> getDatasetsByFileType(String fileType) {
        return datasetThemeRepository.findByFileType(fileType);
    }

    private List<DatasetTheme> getAllDatasets() {
        return datasetThemeRepository.findAll();
    }

    private long getTotalDatasetCount() {
        return datasetThemeRepository.count();
    }

}
