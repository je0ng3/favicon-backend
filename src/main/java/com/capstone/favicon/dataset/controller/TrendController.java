package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.dataset.application.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/trend")
@RequiredArgsConstructor
public class TrendController {

    private final TrendService trendService;

    // 트렌드 데이터 확인(당일 기준으로 조회 하면 됨)
    @GetMapping("/daily")
    public ResponseEntity<APIResponse<?>> getTrendsByDate(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok().body(APIResponse.successAPI("success", trendService.getTrendsByDate(date)));
    }

    // 특정 Dataset의 트렌드 확인용
    @GetMapping("/{datasetId}")
    public ResponseEntity<APIResponse<?>> getTrendsByDatasetId(
            @PathVariable Long datasetId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        return ResponseEntity.ok()
                .body(APIResponse.successAPI("success", trendService.getTrendsByDatasetId(datasetId, startDate, endDate)));
    }

    // 특정 Dataset의 현재 순위 조회
    @GetMapping("/rank/{datasetId}")
    public ResponseEntity<APIResponse<?>> getCurrentRank(@PathVariable Long datasetId) {
        return ResponseEntity.ok()
                .body(APIResponse.successAPI("순위 조회 성공", trendService.getCurrentRank(datasetId)));
    }
}
