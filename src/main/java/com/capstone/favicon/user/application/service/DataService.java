package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.Scrap;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.ScrapResponseDto;

import java.util.List;

public interface DataService {
    ScrapResponseDto addScrap(User user, Long dataId);
    void deleteScrap(User user, Long scrapId);
    List<Scrap> getScrap(User user);
}
