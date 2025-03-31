package com.capstone.favicon.analysis.controller;


import com.capstone.favicon.analysis.application.RegionServiceImpl;
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
        List<String> regionNames = regionServiceImpl.getAllRegionName();
        return ResponseEntity.ok(regionNames);
    }
}
