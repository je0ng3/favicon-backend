package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.RegionService;
import com.capstone.favicon.dataset.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<String> findAllRegionNames() {
        return regionRepository.findAllRegionNames();
    }
}
