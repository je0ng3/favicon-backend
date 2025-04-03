package com.capstone.favicon.dataset.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AnalysisResultDto {
    //private String theme;
    //private String region;
    //private String start;
    //private String end;
    private Map<String, Object> result;
}
