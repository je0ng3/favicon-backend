package com.capston.favicon.user.application.service;

import com.capston.favicon.user.domain.Data;

import java.util.List;

public interface DataService {
    List<Data> search(String text);
    List<Data> searchWithCategory(String text, String category);
}
