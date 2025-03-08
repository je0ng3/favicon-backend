package com.capston.favicon.dataset.service;

import com.capston.favicon.dataset.domain.Dataset;
import com.capston.favicon.dataset.repository.DatasetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetService {
    private final DatasetRepository datasetRepository;

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
        Dataset dataset = datasetRepository.findById(datasetId).orElseThrow(() -> new RuntimeException("데이터셋을 찾을 수 없습니다 datasetId는: " + datasetId));
        dataset.setDownload(dataset.getDownload() + 1);
        datasetRepository.save(dataset);
    }

}