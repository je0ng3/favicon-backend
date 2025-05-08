package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, String> {
    @Query("SELECT r.regionName FROM Region r")
    List<String> findAllRegionNames();
}
