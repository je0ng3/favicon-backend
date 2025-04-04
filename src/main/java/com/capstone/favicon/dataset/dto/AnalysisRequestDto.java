package com.capstone.favicon.dataset.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class AnalysisRequestDto {
    private String theme1;
    private String theme2;
    private String region;
    private YearMonth start;
    private YearMonth end;

}
