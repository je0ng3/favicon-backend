package com.capstone.favicon.dataset.repository;

import com.capstone.favicon.dataset.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, String> {

}
