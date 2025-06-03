package com.capstone.favicon.user.dto;


public class ScrapResponseDto {

    private Long datasetId;
    private String title;
    private String theme;

    public ScrapResponseDto(Long datasetId, String title, String theme) {
        this.datasetId = datasetId;
        this.title = title;
        this.theme = theme;
    }

}
