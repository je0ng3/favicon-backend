package com.capstone.favicon.dataset.dto;

import com.capstone.favicon.dataset.domain.Trend;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TrendResponseDto {

    private Long id;
    private LocalDate rankDate;
    private Integer rank;
    private String trendStatus;
    private Long datasetId;
    private String title;

    public static TrendResponseDto from(Trend trend) {
        return new TrendResponseDto(
                trend.getId(),
                trend.getRankDate(),
                trend.getRank(),
                trend.getTrendStatus(),
                trend.getDataset() == null ? null : trend.getDataset().getDatasetId(),
                trend.getDataset() == null ? null : trend.getDataset().getTitle()
        );
    }
}
