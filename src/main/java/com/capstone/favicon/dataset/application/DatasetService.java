package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

}