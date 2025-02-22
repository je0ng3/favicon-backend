package com.capstone.favicon.Dataset.service;

import com.capstone.favicon.Dataset.domain.DatasetTheme;
import com.capstone.favicon.Dataset.repository.DatasetThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetThemeService {

    private final DatasetThemeRepository datasetThemeRepository;

    @Autowired
    public DatasetThemeService(DatasetThemeRepository datasetThemeRepository) {
        this.datasetThemeRepository = datasetThemeRepository;
    }

    public List<DatasetTheme> getFilteredDatasets(String region, Integer dataYear, String fileType) {
        return datasetThemeRepository.findByRegionAndDataYearAndFileType(region, dataYear, fileType);
    }

    public List<DatasetTheme> getDatasetsByRegionAndDataYear(String region, Integer dataYear) {
        return datasetThemeRepository.findByRegionAndDataYear(region, dataYear);
    }

    public List<DatasetTheme> getDatasetsByRegionAndFileType(String region, String fileType) {
        return datasetThemeRepository.findByRegionAndFileType(region, fileType);
    }

    public List<DatasetTheme> getDatasetsByDataYearAndFileType(Integer dataYear, String fileType) {
        return datasetThemeRepository.findByDataYearAndFileType(dataYear, fileType);
    }

    public List<DatasetTheme> getDatasetsByRegion(String region) {
        return datasetThemeRepository.findByRegion(region);
    }

    public List<DatasetTheme> getDatasetsByDataYear(Integer dataYear) {
        return datasetThemeRepository.findByDataYear(dataYear);
    }

    public List<DatasetTheme> getDatasetsByFileType(String fileType) {
        return datasetThemeRepository.findByFileType(fileType);
    }

    public List<DatasetTheme> getAllDatasets() {
        return datasetThemeRepository.findAll();
    }

    private long getTotalDatasetCount() {
        return datasetThemeRepository.count();
    }

    public Object getThemeRatio() {
        long total = getTotalDatasetCount();

        if (total == 0) {
            return new Object() {
                public final double climate = 0.0;
                public final double environment = 0.0;
                public final double disease = 0.0;
            };
        }

        long climateCount = datasetThemeRepository.countByTheme("기후");
        long environmentCount = datasetThemeRepository.countByTheme("환경");
        long diseaseCount = datasetThemeRepository.countByTheme("질병");

        // 비중 계산
        double climateRatio = (double) climateCount / total;
        double environmentRatio = (double) environmentCount / total;
        double diseaseRatio = (double) diseaseCount / total;

        return new Object() {
            public final double climate = climateRatio;
            public final double environment = environmentRatio;
            public final double disease = diseaseRatio;
        };
    }

}


