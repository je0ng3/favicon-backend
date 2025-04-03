package com.capstone.favicon.dataset.controller;

import com.capstone.favicon.dataset.application.service.AnalysisService;
import com.capstone.favicon.dataset.dto.AnalysisRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> runAnalysis(@RequestBody AnalysisRequestDto requestDto) {
        Map<String, Object> result = analysisService.analyze(requestDto);
        return ResponseEntity.ok(result);
    }
}
