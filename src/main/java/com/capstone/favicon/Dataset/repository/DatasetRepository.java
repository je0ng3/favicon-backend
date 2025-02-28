package com.capstone.favicon.Dataset.repository;

import com.capstone.favicon.Dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findTop10ByOrderByDownloadDesc();
}