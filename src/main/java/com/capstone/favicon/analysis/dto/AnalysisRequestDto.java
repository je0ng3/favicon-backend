package com.capstone.favicon.analysis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class AnalysisRequestDto {
    private String theme;
    private String region;
    private YearMonth start;
    private YearMonth end;

}
