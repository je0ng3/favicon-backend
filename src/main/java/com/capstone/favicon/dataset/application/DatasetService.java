package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;

    private DatasetThemeRepository datasetThemeRepository;

    @Autowired
    public DatasetService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public List<Dataset> findAllDatasets() {
        return datasetRepository.findAll();
    }

    public List<Dataset> getTop10ByDownload() {
        return datasetRepository.findTop10ByOrderByDownloadDesc();
    }

    @Transactional
    public void incrementDownloadCount(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId).orElseThrow(() -> new RuntimeException("Dataset not found with id: " + datasetId));
        dataset.setDownload(dataset.getDownload() + 1);
        datasetRepository.save(dataset);
    }

    public List<DatasetTheme> filterByCategory(String theme) {
        return datasetThemeRepository.findByTheme(theme);
    }

    public long getTotalDatasetCount() {
        return datasetRepository.count();
    }

    public Optional<Dataset> getDatasetDetails(Long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    public Map<String, Double> getThemeRatio() {
        long total = datasetRepository.count();

        if (total == 0) {
            return Map.of(
                    "climate", 0.0,
                    "environment", 0.0,
                    "disease", 0.0
            );
        }

        long climateCount = datasetRepository.countByDatasetTheme_DatasetThemeId(1L);
        long environmentCount = datasetRepository.countByDatasetTheme_DatasetThemeId(2L);
        long diseaseCount = datasetRepository.countByDatasetTheme_DatasetThemeId(3L);

        double climateRatio = (double) climateCount / total;
        double environmentRatio = (double) environmentCount / total;
        double diseaseRatio = (double) diseaseCount / total;

        return Map.of(
                "기후", climateRatio,
                "환경", environmentRatio,
                "질병", diseaseRatio
        );
    }

    public List<Dataset> getDatasetsByCategory(Long datasetThemeId) {
        return datasetRepository.findByDatasetTheme_DatasetThemeId(datasetThemeId);
    }

    public List<Dataset> search(String text) {
        return datasetRepository.searchByText(text);
    }

//    public List<Dataset> searchWithCategory(String text, String category) {
//        return datasetRepository.searchWithCategory(text, category);
//    }

    /***
     * theme(질병, 기후, 환경) 별 세부카테고리(감기, 미세먼지, 기온 등) 목록 조회
     */
    public Map<String, List<String>> getDatasetNameGroupByTheme() {
        List<Dataset> dataset = datasetRepository.findAllWithTheme();

        return dataset.stream()
                .filter(d -> d.getDatasetTheme() != null && d.getDatasetTheme().getTheme() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getDatasetTheme().getTheme(),
                        Collectors.mapping(Dataset::getName, Collectors.toList())
                ));
    }
}