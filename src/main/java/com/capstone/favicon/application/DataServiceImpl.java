package com.capstone.favicon.application;


import com.capstone.favicon.application.repository.DataService;
import com.capstone.favicon.domain.domain.Data;
import com.capstone.favicon.infrastructure.DataRepository;
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
