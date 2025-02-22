package com.capstone.favicon.Dataset.repository;

import com.capstone.favicon.Dataset.domain.DatasetTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetThemeRepository extends JpaRepository<DatasetTheme, Long> {

    List<DatasetTheme> findByRegionAndDataYearAndFileType(String region, int dataYear, String fileType);

    List<DatasetTheme> findByRegionAndDataYear(String region, int dataYear);
    List<DatasetTheme> findByRegionAndFileType(String region, String fileType);
    List<DatasetTheme> findByDataYearAndFileType(int dataYear, String fileType);

    List<DatasetTheme> findByRegion(String region);

    List<DatasetTheme> findByDataYear(int dataYear);

    List<DatasetTheme> findByFileType(String fileType);

    long countByTheme(String theme);
}

