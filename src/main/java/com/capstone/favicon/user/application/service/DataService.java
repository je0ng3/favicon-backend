package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.Scrap;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DataService {
    void addScrap(HttpServletRequest request, Long dataId);
    void deleteScrap(HttpServletRequest request, Long scrapId);
    List<Scrap> getScrap(HttpServletRequest request);
}
