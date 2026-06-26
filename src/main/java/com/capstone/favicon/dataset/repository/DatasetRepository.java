package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    Optional<Dataset> findByS3Key(String s3Key);
    List<Dataset> findAllByOrderByDownloadDesc();
    List<Dataset> findTop9ByOrderByDownloadDesc();
    long countByDatasetTheme_DatasetThemeId(Long datasetThemeId);
    List<Dataset> findByDatasetTheme_DatasetThemeId(Long datasetThemeId);
    Optional<Dataset> findByDatasetThemeAndNameAndOrganization(DatasetTheme datasetTheme, String name, String organization);

    @Query("SELECT d FROM Dataset d JOIN FETCH d.datasetTheme")
    List<Dataset> findAllWithTheme();

    /** 다운로드 수 내림차순 기준 순위(1부터). 자기보다 다운로드가 많은 데이터셋 개수 + 1 을 DB 에서 직접 계산. */
    @Query("SELECT COUNT(d) + 1 FROM Dataset d WHERE COALESCE(d.download, 0) > " +
            "(SELECT COALESCE(d2.download, 0) FROM Dataset d2 WHERE d2.datasetId = :datasetId)")
    long findDownloadRank(@Param("datasetId") Long datasetId);

    /** :from 이후 생성된 데이터셋을 (연, 월) 단위로 묶어 개수를 센다. 반환: [year, month, count] */
    @Query("SELECT YEAR(d.createdDate), MONTH(d.createdDate), COUNT(d) FROM Dataset d " +
            "WHERE d.createdDate >= :from " +
            "GROUP BY YEAR(d.createdDate), MONTH(d.createdDate)")
    List<Object[]> countMonthlyCreatedSince(@Param("from") LocalDate from);

    @Query(value = """
    SELECT * FROM dataset
    WHERE title ILIKE CONCAT('%', :keyword, '%') OR description ILIKE CONCAT('%', :keyword, '%')
    ORDER BY similarity(title, :keyword) DESC, created_at DESC
    """, nativeQuery = true)
    List<Dataset> searchByText(@Param("keyword") String keyword);

    @Query(value = """
    SELECT * FROM dataset
    WHERE title ILIKE CONCAT('%', :keyword, '%') OR description ILIKE CONCAT('%', :keyword, '%')
    AND category = :category
    ORDER BY similarity(title, :keyword) DESC, created_at DESC
    """, nativeQuery = true)
    List<Dataset> searchWithCategory(@Param("keyword") String keyword, @Param("category") String category);

}
