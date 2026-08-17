package com.capstone.favicon.user.dto;

import com.capstone.favicon.user.domain.Scrap;
import lombok.Getter;

@Getter
public class ScrapResponseDto {

    private Long scrapId;
    private Long datasetId;
    private String title;
    private String theme;

    public ScrapResponseDto(Long scrapId, Long datasetId, String title, String theme) {
        this.scrapId = scrapId;
        this.datasetId = datasetId;
        this.title = title;
        this.theme = theme;
    }

    public static ScrapResponseDto from(Scrap scrap) {
        return new ScrapResponseDto(scrap.getScrapId(), scrap.getDatasetId(), scrap.getTitle(), scrap.getTheme());
    }
}
