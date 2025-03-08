package com.capston.favicon.user.application;


import com.capston.favicon.user.application.service.DataService;
import com.capston.favicon.user.domain.Data;
import com.capston.favicon.user.repository.DataRepository;
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
