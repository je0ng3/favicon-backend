package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.dataset.application.service.DatasetThemeService;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data-set")
public class DatasetThemeController {

    private final DatasetThemeService datasetThemeService;

    @Autowired
    public DatasetThemeController(DatasetThemeService datasetThemeService) {
        this.datasetThemeService = datasetThemeService;
    }

    @GetMapping("/filter")
    public ResponseEntity<APIResponse<?>> getDatasets(
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "dataYear", required = false) Integer dataYear,
            @RequestParam(name = "fileType", required = false) String fileType) {

        try {
            List<DatasetTheme> datasets = datasetThemeService.getDatasets(region, dataYear, fileType);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

}
