package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.config.APIResponse;
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
        List<Trend> trends = trendRepository.findAllByRankDate(date);
        if (trends.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.errorAPI("찾을 수 없음"));
        }
        return ResponseEntity.ok().body(APIResponse.successAPI("success", trends));
    }

    // 특정 Dataset의 트렌드 확인용
    @GetMapping("/{datasetId}")
    public ResponseEntity<APIResponse<?>> getTrendsByDatasetId(
            @PathVariable Long datasetId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        List<Trend> trends = trendRepository.findByDatasetIdAndDateRange(datasetId, startDate, endDate);
        if (trends.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.errorAPI("찾을 수 없음"));
        }
        return ResponseEntity.ok().body(APIResponse.successAPI("success", trends));
    }

    // 특정 Dataset의 현재 순위 조회
    @GetMapping("/rank/{datasetId}")
    public ResponseEntity<APIResponse<?>> getCurrentRank(@PathVariable Long datasetId) {
        if (!datasetRepository.existsById(datasetId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.errorAPI("찾을 수 없음"));
        }
        long rank = datasetRepository.findDownloadRank(datasetId); // 전체 로드 없이 DB 에서 순위 계산
        return ResponseEntity.ok().body(APIResponse.successAPI("순위 조회 성공", rank));
    }

}
