package com.capstone.favicon.dataset.dto;

import com.capstone.favicon.dataset.domain.DatasetTheme;
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

    public DatasetThemeDto(Long datasetThemeId, String theme, String region, Integer dataYear, String fileType) {
        this.datasetThemeId = datasetThemeId;
        this.theme = theme;
        this.region = region;
        this.dataYear = dataYear;
        this.fileType = fileType;
    }

    public static DatasetThemeDto from(DatasetTheme datasetTheme) {
        return new DatasetThemeDto(
                datasetTheme.getDatasetThemeId(),
                datasetTheme.getTheme(),
                datasetTheme.getRegion(),
                datasetTheme.getDataYear(),
                datasetTheme.getFileType()
        );
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
