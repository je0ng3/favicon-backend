package com.capston.favicon.presentation.controller;


import com.capston.favicon.application.repository.DataService;
import com.capston.favicon.config.APIResponse;
import com.capston.favicon.domain.domain.Data;
import com.capston.favicon.domain.dto.SearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("/data-table/search-sorted")
    public ResponseEntity<APIResponse<?>> search(@RequestBody SearchDto searchDto) {
        try {
            List<Data> dataList = dataService.search(searchDto.getText());
            return ResponseEntity.ok().body(APIResponse.successAPI("검색결과", dataList));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }

    }

}
