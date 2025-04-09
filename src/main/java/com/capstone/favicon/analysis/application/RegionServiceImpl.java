package com.capstone.favicon.analysis.application;

import com.capstone.favicon.analysis.application.service.RegionService;
import com.capstone.favicon.analysis.repository.RegionRepository;
import com.capstone.favicon.dataset.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capstone.favicon.analysis.domain.Region;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }
}
