package com.capstone.favicon.dataset.dto;

import lombok.Getter;

@Getter
public class DatasetThemeDto {
    private Long datasetThemeId;
    private String theme;
    private String region;
    private Integer dataYear;
    private String fileType;

    public DatasetThemeDto(Long datasetThemeId, String theme) {
        this.datasetThemeId = datasetThemeId;
        this.theme = theme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatasetThemeDto)) return false;
        DatasetThemeDto that = (DatasetThemeDto) o;
        return datasetThemeId.equals(that.datasetThemeId);
    }

    @Override
    public int hashCode() {
        return datasetThemeId.hashCode();
    }
}
