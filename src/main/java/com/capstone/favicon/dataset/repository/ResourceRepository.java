package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByDatasetDatasetId(Long datasetId);
}
