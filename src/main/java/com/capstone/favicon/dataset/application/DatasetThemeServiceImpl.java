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
    public List<DatasetTheme> getFilteredDatasets(String region, Integer dataYear, String fileType) {
        return datasetThemeRepository.findByRegionAndDataYearAndFileType(region, dataYear, fileType);
    }

    @Override
    public List<DatasetTheme> getDatasetsByRegionAndDataYear(String region, Integer dataYear) {
        return datasetThemeRepository.findByRegionAndDataYear(region, dataYear);
    }

    @Override
    public List<DatasetTheme> getDatasetsByRegionAndFileType(String region, String fileType) {
        return datasetThemeRepository.findByRegionAndFileType(region, fileType);
    }

    @Override
    public List<DatasetTheme> getDatasetsByDataYearAndFileType(Integer dataYear, String fileType) {
        return datasetThemeRepository.findByDataYearAndFileType(dataYear, fileType);
    }

    @Override
    public List<DatasetTheme> getDatasetsByRegion(String region) {
        return datasetThemeRepository.findByRegion(region);
    }

    @Override
    public List<DatasetTheme> getDatasetsByDataYear(Integer dataYear) {
        return datasetThemeRepository.findByDataYear(dataYear);
    }

    @Override
    public List<DatasetTheme> getDatasetsByFileType(String fileType) {
        return datasetThemeRepository.findByFileType(fileType);
    }

    @Override
    public List<DatasetTheme> getAllDatasets() {
        return datasetThemeRepository.findAll();
    }

    private long getTotalDatasetCount() {
        return datasetThemeRepository.count();
    }

}
