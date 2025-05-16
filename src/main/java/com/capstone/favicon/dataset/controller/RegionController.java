package com.capstone.favicon.dataset.controller;


import com.capstone.favicon.dataset.application.service.RegionService;
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
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<List<String>> getAllRegions() {
        List<String> regions = regionService.findAllRegionNames();
        return ResponseEntity.ok(regions);
    }
}
