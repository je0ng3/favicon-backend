package com.capstone.favicon.analysis.service;

import com.capstone.favicon.analysis.repository.RegionRepository;
import org.springframework.stereotype.Service;
import com.capstone.favicon.analysis.domain.Region;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<String> getAllRegionName() {
        return regionRepository.findAll().stream()
                .map(Region::getRegionName)
                .collect(Collectors.toList());
    }
}
