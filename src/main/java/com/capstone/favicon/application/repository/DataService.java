package com.capstone.favicon.application.repository;

import com.capstone.favicon.domain.domain.Data;

import java.util.List;

public interface DataService {
    List<Data> search(String text);
    List<Data> searchWithCategory(String text, String category);
}
