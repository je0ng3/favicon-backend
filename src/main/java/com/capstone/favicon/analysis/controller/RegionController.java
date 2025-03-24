package com.capstone.favicon.analysis.controller;


import com.capstone.favicon.analysis.service.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/region")
public class RegionController {
    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllRegions() {
        List<String> regionNames = regionService.getAllRegionName();
        return ResponseEntity.ok(regionNames);
    }
}
