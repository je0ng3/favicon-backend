package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Trend;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrendRepository extends JpaRepository<Trend, Long> {
    @Query("SELECT t FROM Trend t WHERE t.dataset.datasetId = :datasetId AND t.rankDate = :date")
    Optional<Trend> findByDatasetIdAndDate(@Param("datasetId") Long datasetId, @Param("date") LocalDate date);
    List<Trend> findAllByRankDate(LocalDate rankDate);

    /** 특정 날짜의 (datasetId, rank) 쌍만 조회. 엔티티/연관을 로딩하지 않아 EAGER N+1 이 없다.
     *  명시적 INNER JOIN 이라 dataset 이 null 인 행은 제외된다(경로식 t.dataset.datasetId 는 FK 를
     *  조인 없이 읽어 null 행까지 포함되므로 반드시 JOIN 으로 강제해야 한다). */
    @Query("SELECT d.datasetId, t.rank FROM Trend t JOIN t.dataset d WHERE t.rankDate = :date")
    List<Object[]> findDatasetRankPairsByDate(@Param("date") LocalDate date);
    @Query("SELECT t FROM Trend t WHERE t.dataset.datasetId = :datasetId AND t.rankDate BETWEEN :startDate AND :endDate")
    List<Trend> findByDatasetIdAndDateRange(@Param("datasetId") Long datasetId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}