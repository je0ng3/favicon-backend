package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trend")
@RequiredArgsConstructor
public class TrendController {

    private final TrendRepository trendRepository;
    private final DatasetRepository datasetRepository;

    // 트렌드 데이터 확인(당일 기준으로 조회 하면 됨)
    @GetMapping("/daily")
    public ResponseEntity<APIResponse<?>> getTrendsByDate(@RequestParam("date") LocalDate date) {
        try {
            List<Trend> trends = trendRepository.findAllByRankDate(date);
            if (trends.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.errorAPI("찾을 수 없음"));
            }
            return ResponseEntity.ok().body(APIResponse.successAPI("success", trends));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    // 특정 Dataset의 트렌드 확인용
    @GetMapping("/{datasetId}")
    public ResponseEntity<APIResponse<?>> getTrendsByDatasetId(
            @PathVariable Long datasetId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        try {
            List<Trend> trends = trendRepository.findByDatasetIdAndDateRange(datasetId, startDate, endDate);
            if (trends.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.errorAPI("찾을 수 없음"));
            }
            return ResponseEntity.ok().body(APIResponse.successAPI("success", trends));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    // 특정 Dataset의 현재 순위 조회
    @GetMapping("/rank/{datasetId}")
    public ResponseEntity<APIResponse<?>> getCurrentRank(@PathVariable Long datasetId) {
        List<Dataset> datasets = datasetRepository.findAllByOrderByDownloadDesc();
        try {
            for (int i = 0; i < datasets.size(); i++) {
                if (datasets.get(i).getDatasetId().equals(datasetId)) {
                    return ResponseEntity.ok().body(APIResponse.successAPI("순위 조회 성공", i+1)); // 순위는 1부터 시작
                }
            }
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.errorAPI("찾을 수 없음"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

}
