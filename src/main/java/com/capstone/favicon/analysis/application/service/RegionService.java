package com.capstone.favicon.analysis.application.service;

import com.capstone.favicon.analysis.domain.Region;
import com.capstone.favicon.analysis.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface RegionService {
    List<String> getAllRegionName();
}
