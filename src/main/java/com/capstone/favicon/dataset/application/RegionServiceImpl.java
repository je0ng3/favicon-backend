package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.RegionService;
import com.capstone.favicon.dataset.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capstone.favicon.dataset.domain.Region;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<String> getAllRegionName() {
        return regionRepository.findAll().stream()
                .map(Region::getRegionName)
                .collect(Collectors.toList());
    }
}
