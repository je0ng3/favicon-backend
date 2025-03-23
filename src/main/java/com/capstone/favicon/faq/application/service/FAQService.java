package com.capstone.favicon.faq.application.service;

import com.capstone.favicon.faq.dto.FAQRequestDto;
import com.capstone.favicon.faq.dto.FAQResponseDto;
import java.util.List;

public interface FAQService {
    void createFAQ(Long userId, FAQRequestDto request);
    void updateFAQ(Long faqId, FAQRequestDto request);
    void deleteFAQ(Long faqId);
    List<FAQResponseDto> getAllFAQs();
    FAQResponseDto getFAQById(Long faqId);
}

