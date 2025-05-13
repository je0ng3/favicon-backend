package com.capstone.favicon.dataset.controller;


import com.capstone.favicon.dataset.application.RegionServiceImpl;
import com.capstone.favicon.dataset.domain.Region;
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
    public ResponseEntity<List<String>> getAllRegions() {
        List<String> regions = regionServiceImpl.findAllRegionNames();
        return ResponseEntity.ok(regions);
    }
}
