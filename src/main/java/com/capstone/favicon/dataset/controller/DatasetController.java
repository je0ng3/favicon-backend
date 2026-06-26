package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.dataset.application.service.DatasetService;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import org.springframework.http.ResponseEntity;
import com.capstone.favicon.dataset.dto.SearchDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/data-set")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    public ResponseEntity<APIResponse<?>> getDatasets() {
        List<Dataset> datasets = datasetService.findAllDatasets();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
    }

    @GetMapping("/top9")
    public ResponseEntity<APIResponse<?>> getTop9Datasets() {
        List<Dataset> datasets = datasetService.getTop9ByDownload();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
    }

    @PostMapping("/incrementDownload/{datasetId}")
    public ResponseEntity<APIResponse<?>> incrementDownload(@PathVariable Long datasetId) {
        datasetService.incrementDownloadCount(datasetId);
        return ResponseEntity.ok().body(APIResponse.successAPI("success", null));
    }

    @GetMapping("/theme")
    public ResponseEntity<APIResponse<?>> filterByCategory(@RequestParam String theme) {
        List<DatasetTheme> datasetThemes = datasetService.filterByCategory(theme);
        return ResponseEntity.ok().body(APIResponse.successAPI("success", datasetThemes));
    }

    @GetMapping("/count")
    public ResponseEntity<APIResponse<?>> getTotalDatasetCount() {
        long count = datasetService.getTotalDatasetCount();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", count));
    }

    @GetMapping("/{datasetId:\\d+}")
    public ResponseEntity<APIResponse<?>> getDatasetDetails(@PathVariable Long datasetId) {
        Optional<Dataset> dataset = datasetService.getDatasetDetails(datasetId);
        return ResponseEntity.ok().body(APIResponse.successAPI("success", dataset));
    }

    @GetMapping("/ratio")
    public ResponseEntity<APIResponse<?>> getThemeStats() {
        Map<String, Map<String, Object>> themeStats = datasetService.getThemeStats();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", themeStats));
    }

    @GetMapping("/category/{themeId}")
    public ResponseEntity<APIResponse<?>> getDatasetsByCategory(@PathVariable Long themeId) {
        List<Dataset> datasets = datasetService.getDatasetsByCategory(themeId);
        return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
    }

    @GetMapping("/search-sorted")
    public ResponseEntity<APIResponse<?>> search(@RequestBody SearchDto searchDto) {
        List<Dataset> dataList = datasetService.search(searchDto.getText());
        return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
    }

    @GetMapping("/group-by-theme")
    public ResponseEntity<APIResponse<?>> getDatasetsGroupedByTheme() {
        Map<String, List<String>> result = datasetService.getDatasetNameGroupByTheme();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", result));
    }

    @GetMapping("/stats")
    public ResponseEntity<APIResponse<?>> getMonthlyStats() {
        Map<String, Map<String, Object>> stats = datasetService.getMonthlyDatasetStats();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", stats));
    }

}
