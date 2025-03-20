package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findTop10ByOrderByDownloadDesc();
}