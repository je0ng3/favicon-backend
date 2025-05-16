package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.DatasetService;
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
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRepository datasetRepository;

    private DatasetThemeRepository datasetThemeRepository;

    @Autowired
    public DatasetServiceImpl(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    @Override
    public List<Dataset> findAllDatasets() {
        return datasetRepository.findAll();
    }

    @Override
    public List<Dataset> getTop10ByDownload() {
        return datasetRepository.findTop10ByOrderByDownloadDesc();
    }

    @Transactional
    @Override
    public void incrementDownloadCount(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId).orElseThrow(() -> new RuntimeException("Dataset not found with id: " + datasetId));
        dataset.setDownload(dataset.getDownload() + 1);
        datasetRepository.save(dataset);
    }

    @Override
    public List<DatasetTheme> filterByCategory(String theme) {
        return datasetThemeRepository.findByTheme(theme);
    }

    @Override
    public long getTotalDatasetCount() {
        return datasetRepository.count();
    }

    @Override
    public Optional<Dataset> getDatasetDetails(Long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    @Override
    public Map<String, Map<String, Object>> getThemeStats() {
        long total = datasetRepository.count();

        if (total == 0) {
            return Map.of(
                    "기후", Map.of("count", 0, "ratio", 0),
                    "환경", Map.of("count", 0, "ratio", 0),
                    "질병", Map.of("count", 0, "ratio", 0)
            );
        }

        long climateCount = datasetRepository.countByDatasetTheme_DatasetThemeId(1L);
        long environmentCount = datasetRepository.countByDatasetTheme_DatasetThemeId(2L);
        long diseaseCount = datasetRepository.countByDatasetTheme_DatasetThemeId(3L);

        int climateRatio = (int) Math.round((double) climateCount / total * 100);
        int environmentRatio = (int) Math.round((double) environmentCount / total * 100);
        int diseaseRatio = (int) Math.round((double) diseaseCount / total * 100);

        return Map.of(
                "기후", Map.of("count", climateCount, "ratio", climateRatio),
                "환경", Map.of("count", environmentCount, "ratio", environmentRatio),
                "질병", Map.of("count", diseaseCount, "ratio", diseaseRatio)
        );
    }

    @Override
    public List<Dataset> getDatasetsByCategory(Long datasetThemeId) {
        return datasetRepository.findByDatasetTheme_DatasetThemeId(datasetThemeId);
    }

    @Override
    public List<Dataset> search(String text) {
        return datasetRepository.searchByText(text);
    }

//    public List<Dataset> searchWithCategory(String text, String category) {
//        return datasetRepository.searchWithCategory(text, category);
//    }

    /***
     * theme(질병, 기후, 환경) 별 세부카테고리(감기, 미세먼지, 기온 등) 목록 조회
     */
    @Override
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