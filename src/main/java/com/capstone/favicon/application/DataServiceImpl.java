package com.capston.favicon.application;


import com.capston.favicon.application.service.DataService;
import com.capston.favicon.domain.domain.Data;
import com.capston.favicon.infrastructure.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataRepository dataRepository;

    @Override
    public List<Data> search(String text) {
        return dataRepository.searchByText(text);
    }

    @Override
    public List<Data> searchWithCategory(String text, String category) {
        return dataRepository.searchWithCategory(text, category);
    }

}
