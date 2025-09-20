package com.capstone.favicon.admin.application;

import com.capstone.favicon.admin.application.service.FAQService;
import com.capstone.favicon.admin.domain.FAQ;
import com.capstone.favicon.admin.dto.FAQRequestDto;
import com.capstone.favicon.admin.dto.FAQResponseDto;
import com.capstone.favicon.admin.repository.FAQRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;

    @Override
    public void createFAQ(FAQRequestDto request) {
        FAQ faq = new FAQ();
        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());

        faqRepository.save(faq);
    }

    @Override
    public void updateFAQ(Long faqId, FAQRequestDto request) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));

        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());

        faqRepository.save(faq);
    }

    @Override
    public void deleteFAQ(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        faqRepository.delete(faq);
    }

    @Override
    public List<FAQResponseDto> getAllFAQs() {
        List<FAQ> faqs = faqRepository.findAll();
        return faqs.stream().map(FAQResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public FAQResponseDto getFAQById(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        return new FAQResponseDto(faq);
    }

}