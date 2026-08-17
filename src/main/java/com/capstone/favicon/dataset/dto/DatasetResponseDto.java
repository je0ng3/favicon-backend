package com.capstone.favicon.dataset.dto;

import com.capstone.favicon.dataset.domain.Dataset;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/** 엔티티를 그대로 내보내면 downloadSet 이 직렬화 중에 지연 로딩되어 N+1 이 난다.
 *  resource 는 mappedBy @OneToOne 이라 어차피 조회 시점에 함께 로딩되므로 평탄화해서 유지한다. */
@Getter
@AllArgsConstructor
public class DatasetResponseDto {

    private Long datasetId;
    private String name;
    private String title;
    private String organization;
    private String description;
    private LocalDate createdDate;
    private LocalDate updateDate;
    private Integer view;
    private Integer download;
    private String license;
    private String keyword;
    private Boolean analysis;
    private String s3Key;
    private Long datasetThemeId;
    private String theme;
    private Long resourceId;
    private String resourceName;
    private String resourceType;
    private String resourceUrl;

    public static DatasetResponseDto from(Dataset dataset) {
        return new DatasetResponseDto(
                dataset.getDatasetId(),
                dataset.getName(),
                dataset.getTitle(),
                dataset.getOrganization(),
                dataset.getDescription(),
                dataset.getCreatedDate(),
                dataset.getUpdateDate(),
                dataset.getView(),
                dataset.getDownload(),
                dataset.getLicense(),
                dataset.getKeyword(),
                dataset.getAnalysis(),
                dataset.getS3Key(),
                dataset.getDatasetTheme() == null ? null : dataset.getDatasetTheme().getDatasetThemeId(),
                dataset.getDatasetTheme() == null ? null : dataset.getDatasetTheme().getTheme(),
                dataset.getResource() == null ? null : dataset.getResource().getResourceId(),
                dataset.getResource() == null ? null : dataset.getResource().getResourceName(),
                dataset.getResource() == null || dataset.getResource().getType() == null
                        ? null : dataset.getResource().getType().name(),
                dataset.getResource() == null ? null : dataset.getResource().getResourceUrl()
        );
    }
}
