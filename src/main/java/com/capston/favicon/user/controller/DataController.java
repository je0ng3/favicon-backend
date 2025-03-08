package com.capston.favicon.user.controller;


import com.capston.favicon.user.application.service.DataService;
import com.capston.favicon.config.APIResponse;
import com.capston.favicon.user.domain.Data;
import com.capston.favicon.user.dto.SearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("/data-set/search-sorted")
    public ResponseEntity<APIResponse<?>> search(@RequestBody SearchDto searchDto) {
        try {
            List<Data> dataList = dataService.search(searchDto.getText());
            return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

    @GetMapping("/data-set/search-sorted/{category}")
    public ResponseEntity<APIResponse<?>> searchWithCategory(@PathVariable("category") String category, @RequestBody SearchDto searchDto) {
        try {
            List<Data> dataList = dataService.searchWithCategory(searchDto.getText(), category);
            return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

}
