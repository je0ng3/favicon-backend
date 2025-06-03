package com.capstone.favicon.user.controller;


import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.user.application.service.DataService;
import com.capstone.favicon.user.domain.Scrap;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class DataAccessController {

    @Autowired
    private DataService dataService;

    @PostMapping("/scrap/{data-id}")
    public ResponseEntity<APIResponse<?>> addScrap(@PathVariable("data-id") Long dataId, HttpServletRequest request) {
        try {
            dataService.addScrap(request, dataId);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @DeleteMapping("/scrap/{scrap-id}")
    public ResponseEntity<APIResponse<?>> deleteScrap(@PathVariable("scrap-id") Long scrapId, HttpServletRequest request) {
        try {
            dataService.deleteScrap(request, scrapId);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/scrap")
    public ResponseEntity<APIResponse<?>> getScraps(HttpServletRequest request) {
        try {
            List<Scrap> scraps = dataService.getScrap(request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", scraps));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }



}
