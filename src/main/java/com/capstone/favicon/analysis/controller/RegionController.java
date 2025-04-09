package com.capstone.favicon.analysis.controller;


import com.capstone.favicon.analysis.application.RegionServiceImpl;
import com.capstone.favicon.analysis.domain.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private RegionServiceImpl regionServiceImpl;

    @GetMapping
    public ResponseEntity<List<Region>> getAllRegions() {
        List<Region> regions = regionServiceImpl.getAllRegions();
        return ResponseEntity.ok(regions);
    }
}
