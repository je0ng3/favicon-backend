package com.capston.favicon.dataset.repository;

import com.capston.favicon.dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findTop10ByOrderByDownloadDesc();
}