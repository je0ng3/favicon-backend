package com.capstone.favicon.analysis.application.service;

import com.capstone.favicon.analysis.domain.Region;

import java.util.List;
import java.util.stream.Collectors;

public interface RegionService {
    List<Region> getAllRegions();
}
