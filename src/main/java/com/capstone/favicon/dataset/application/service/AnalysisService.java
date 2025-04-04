package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.dto.AnalysisRequestDto;

import java.util.Map;

public interface AnalysisService {
    Map<String, Object> analyze(AnalysisRequestDto requestDto);
}
