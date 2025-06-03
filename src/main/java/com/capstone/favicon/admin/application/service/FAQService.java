package com.capstone.favicon.admin.application.service;

import com.capstone.favicon.admin.dto.FAQRequestDto;
import com.capstone.favicon.admin.dto.FAQResponseDto;
import com.capstone.favicon.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FAQService {
    User getAdminUserFromSession(HttpServletRequest request);

    void createFAQ(FAQRequestDto request);
    void updateFAQ(Long faqId, FAQRequestDto request);
    void deleteFAQ(Long faqId);
    List<FAQResponseDto> getAllFAQs();
    FAQResponseDto getFAQById(Long faqId);
}

