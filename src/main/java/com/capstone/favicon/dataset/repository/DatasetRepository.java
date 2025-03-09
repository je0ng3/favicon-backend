package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findTop10ByOrderByDownloadDesc();
}