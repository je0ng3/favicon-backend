package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.DataRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DataRequestRepository extends JpaRepository<DataRequest, Long> {
    List<DataRequest> findByReviewStatus(DataRequest.ReviewStatus reviewStatus);
}