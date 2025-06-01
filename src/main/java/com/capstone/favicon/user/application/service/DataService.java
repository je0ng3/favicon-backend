package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.Scrap;
import com.capstone.favicon.user.dto.ScrapResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DataService {
    ScrapResponseDto addScrap(HttpServletRequest request, Long dataId);
    void deleteScrap(HttpServletRequest request, Long scrapId);
    List<Scrap> getScrap(HttpServletRequest request);
}
