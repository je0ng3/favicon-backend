package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.service.DatasetThemeService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<DatasetTheme> getFilteredDatasets(
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "dataYear", required = false) Integer dataYear,
            @RequestParam(name = "fileType", required = false) String fileType) {


        if (region != null && dataYear != null && fileType != null) {
            return datasetThemeService.getFilteredDatasets(region, dataYear, fileType);
        } else if (region != null && dataYear != null) {
            return datasetThemeService.getDatasetsByRegionAndDataYear(region, dataYear);
        } else if (region != null && fileType != null) {
            return datasetThemeService.getDatasetsByRegionAndFileType(region, fileType);
        } else if (dataYear != null && fileType != null) {
            return datasetThemeService.getDatasetsByDataYearAndFileType(dataYear, fileType);
        } else if (region != null) {
            return datasetThemeService.getDatasetsByRegion(region);
        } else if (dataYear != null) {
            return datasetThemeService.getDatasetsByDataYear(dataYear);
        } else if (fileType != null) {
            return datasetThemeService.getDatasetsByFileType(fileType);
        } else {
            return datasetThemeService.getAllDatasets();
        }
    }

    @GetMapping("/ratio")
    public Object getThemeRatio() {
        return datasetThemeService.getThemeRatio();
    }

}


