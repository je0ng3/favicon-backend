package com.capstone.favicon.analysis.repository;

import com.capstone.favicon.analysis.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, String> {

}
