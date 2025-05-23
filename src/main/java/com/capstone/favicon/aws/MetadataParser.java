package com.capstone.favicon.aws;

import com.capstone.favicon.dataset.domain.DatasetTheme;
import lombok.Getter;

import java.util.List;

public class MetadataParser {

    public static DatasetMetadata extractMetadata(String fileName, List<DatasetTheme> themes) {
        String pureFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        System.out.println("=== [DEBUG] 메타데이터 파싱용 파일명: " + pureFileName);
        String[] parts = pureFileName.split("_");

        if (parts.length < 4) {
            throw new IllegalArgumentException("파일명 형식이 올바르지 않습니다: " + fileName);
        }

        String themeName = parts[0];
        String datasetName = parts[1];
        String organization = parts[2];
        String type = extractFileType(pureFileName);

        String titleWithoutExtension = pureFileName.substring(0, pureFileName.lastIndexOf('.'));
        String[] titleParts = titleWithoutExtension.split("_");

        // title의 맨끝 지역 이름
        String region = titleParts[titleParts.length - 1];

        // title의 2번째 부분 (index=1) 세부 카테고리
        String subCategory = titleParts.length > 2 ? titleParts[1] : "";

        Long datasetThemeId = themes.stream()
                .filter(t -> t.getTheme().equals(themeName))
                .map(DatasetTheme::getDatasetThemeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다: " + themeName));

        String description = organization + "에서 수집한 " + region + "의 " + subCategory + " 데이터 입니다";


        return new DatasetMetadata(datasetThemeId, datasetName, titleWithoutExtension, organization, type, description);
    }

    private static String extractFileType(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "UNKNOWN";
        }
        return fileName.substring(lastDotIndex + 1).toUpperCase();
    }

    @Getter
    public static class DatasetMetadata {
        private final Long datasetThemeId;
        private final String name;
        private final String title;
        private final String organization;
        private final String type;
        private final String description;

        public DatasetMetadata(Long datasetThemeId, String name, String title, String organization, String type, String description) {
            this.datasetThemeId = datasetThemeId;
            this.name = name;
            this.title = title;
            this.organization = organization;
            this.type = type;
            this.description = description;
        }
    }
}