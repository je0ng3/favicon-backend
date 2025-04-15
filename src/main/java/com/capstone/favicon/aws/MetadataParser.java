package com.capstone.favicon.aws;

import com.capstone.favicon.dataset.domain.DatasetTheme;
import lombok.Getter;

import java.util.List;

public class MetadataParser {

    public static DatasetMetadata extractMetadata(String fileName, List<DatasetTheme> themes) {
        String[] parts = fileName.split("_");

        if (parts.length < 3) {
            throw new IllegalArgumentException("파일명 형식이 올바르지 않습니다: " + fileName);
        }

        String themeName = parts[0];
        String datasetName = parts[1];
        String organization = parts[parts.length - 1];
        String type = extractFileType(fileName);

        Long datasetThemeId = themes.stream()
                .filter(t -> t.getTheme().equals(themeName))
                .map(DatasetTheme::getDatasetThemeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다: " + themeName));

        return new DatasetMetadata(datasetThemeId, datasetName, fileName, organization, type);
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

        public DatasetMetadata(Long datasetThemeId, String name, String title, String organization, String type) {
            this.datasetThemeId = datasetThemeId;
            this.name = name;
            this.title = title;
            this.organization = organization;
            this.type = type;
        }
    }
}