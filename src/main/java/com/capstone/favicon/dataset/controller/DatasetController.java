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
        try {
            List<Dataset> datasets = datasetService.findAllDatasets();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/top10")
    public ResponseEntity<APIResponse<?>> getTop10Datasets() {
        try {
            List<Dataset> datasets = datasetService.getTop10ByDownload();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/incrementDownload/{datasetId}")
    public ResponseEntity<APIResponse<?>> incrementDownload(@PathVariable Long datasetId) {
        try {
            datasetService.incrementDownloadCount(datasetId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/theme")
    public ResponseEntity<APIResponse<?>> filterByCategory(@RequestParam String theme) {
        try {
            List<DatasetTheme> datasetThemes = datasetService.filterByCategory(theme);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", datasetThemes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<APIResponse<?>> getTotalDatasetCount() {
        try {
            long count = datasetService.getTotalDatasetCount();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/{datasetId:\\d+}")
    public ResponseEntity<APIResponse<?>> getDatasetDetails(@PathVariable Long datasetId) {
        try {
            Optional<Dataset> dataset =  datasetService.getDatasetDetails(datasetId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", dataset));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/ratio")
    public ResponseEntity<APIResponse<?>> getThemeStats() {
        try {
            Map<String, Map<String, Object>> themeStats = datasetService.getThemeStats();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", themeStats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/category/{themeId}")
    public ResponseEntity<APIResponse<?>> getDatasetsByCategory(@PathVariable Long themeId) {
        try {
            List<Dataset> datasets = datasetService.getDatasetsByCategory(themeId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", datasets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/search-sorted")
    public ResponseEntity<APIResponse<?>> search(@RequestBody SearchDto searchDto) {
        try {
            List<Dataset> dataList = datasetService.search(searchDto.getText());
            return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

    @GetMapping("/group-by-theme")
    public ResponseEntity<APIResponse<?>> getDatasetsGroupedByTheme() {
        try {
            Map<String, List<String>> result = datasetService.getDatasetNameGroupByTheme();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

//    @GetMapping("/search-sorted/{category}")
//    public ResponseEntity<APIResponse<?>> searchWithCategory(@PathVariable("category") String category, @RequestBody SearchDto searchDto) {
//        try {
//            List<Dataset> dataList = datasetService.searchWithCategory(searchDto.getText(), category);
//            return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
//        } catch (Exception e) {
//            String message = e.getMessage();
//            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
//        }
//    }

}