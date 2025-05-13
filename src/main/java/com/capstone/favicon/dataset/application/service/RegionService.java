package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.Region;
import com.capstone.favicon.dataset.repository.RegionRepository;

import java.util.List;

public interface RegionService {
    List<String> findAllRegionNames();
}
