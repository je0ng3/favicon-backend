package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.DataRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DataRequestRepository extends JpaRepository<DataRequest, Long> {
    List<DataRequest> findByReviewStatus(DataRequest.ReviewStatus reviewStatus);

    long countByReviewStatus(DataRequest.ReviewStatus reviewStatus);

    /** :from 이후 요청을 (연, 월) 단위로 묶어 개수를 센다. 반환: [year, month, count] */
    @Query("SELECT YEAR(r.uploadDate), MONTH(r.uploadDate), COUNT(r) FROM DataRequest r " +
            "WHERE r.uploadDate >= :from " +
            "GROUP BY YEAR(r.uploadDate), MONTH(r.uploadDate)")
    List<Object[]> countMonthlySince(@Param("from") LocalDate from);

    /** :from 이후 특정 상태의 요청을 (연, 월) 단위로 묶어 개수를 센다. 반환: [year, month, count] */
    @Query("SELECT YEAR(r.uploadDate), MONTH(r.uploadDate), COUNT(r) FROM DataRequest r " +
            "WHERE r.uploadDate >= :from AND r.reviewStatus = :status " +
            "GROUP BY YEAR(r.uploadDate), MONTH(r.uploadDate)")
    List<Object[]> countMonthlyByStatusSince(@Param("from") LocalDate from,
                                             @Param("status") DataRequest.ReviewStatus status);
}
