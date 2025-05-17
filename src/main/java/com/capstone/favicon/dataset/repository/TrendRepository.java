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
    @Query("SELECT t FROM Trend t WHERE t.dataset.datasetId = :datasetId AND t.rankDate BETWEEN :startDate AND :endDate")
    List<Trend> findByDatasetIdAndDateRange(@Param("datasetId") Long datasetId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}