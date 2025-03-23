package com.capstone.favicon.faq.application.service;

import com.capstone.favicon.faq.dto.FAQRequestDto;

public interface FAQService {
    void createFAQ(Long userId, FAQRequestDto request);
    void updateFAQ(Long faqId, FAQRequestDto request);
    void deleteFAQ(Long faqId);
}

