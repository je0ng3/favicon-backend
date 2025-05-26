package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByDatasetAndResourceName(Dataset dataset, String resourceName);
    Optional<Resource> findByDatasetDatasetId(Long datasetId);
    List<Resource> findByDataset(Dataset dataset);
}
