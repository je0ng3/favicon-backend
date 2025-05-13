package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
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

    // 트렌드 데이터 확인 (특정 날짜 기준)
    @GetMapping("/daily")
    public ResponseEntity<List<Trend>> getTrendsByDate(@RequestParam("date") LocalDate date) {
        List<Trend> trends = trendRepository.findAllByRankDate(date);
        if (trends.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(trends);
    }

    // 특정 Dataset의 트렌드 확인
    @GetMapping("/{datasetId}")
    public ResponseEntity<List<Trend>> getTrendsByDatasetId(
            @PathVariable Long datasetId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        List<Trend> trends = trendRepository.findByDatasetIdAndDateRange(datasetId, startDate, endDate);
        if (trends.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(trends);
    }

    // 특정 Dataset의 현재 순위 조회
    @GetMapping("/rank/{datasetId}")
    public ResponseEntity<Integer> getCurrentRank(@PathVariable Long datasetId) {
        List<Dataset> datasets = datasetRepository.findAllByOrderByDownloadDesc();
        for (int i = 0; i < datasets.size(); i++) {
            if (datasets.get(i).getDatasetId().equals(datasetId)) {
                return ResponseEntity.ok(i + 1); // 순위는 1부터 시작
            }
        }
        return ResponseEntity.notFound().build();
    }

}
