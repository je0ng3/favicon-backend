package com.capstone.favicon.Dataset.controller;

import com.capstone.favicon.Dataset.domain.Dataset;
import com.capstone.favicon.Dataset.service.DatasetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    public List<Dataset> getDatasets() {
        return datasetService.findAllDatasets();
    }

    @GetMapping("/top10")
    public List<Dataset> getTop10Datasets() {
        return datasetService.getTop10ByDownload();
    }

    @PostMapping("/incrementDownload/{datasetId}")
    public void incrementDownload(@PathVariable Long datasetId) {
        datasetService.incrementDownloadCount(datasetId);
    }

}
