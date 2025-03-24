package com.capstone.favicon.analysis.application.service;

import com.capstone.favicon.analysis.dto.AnalysisRequestDto;
import com.capstone.favicon.analysis.dto.AnalysisResultDto;

import java.util.Map;

public interface AnalysisService {
    Map<String, Object> analyze(AnalysisRequestDto requestDto);
}
