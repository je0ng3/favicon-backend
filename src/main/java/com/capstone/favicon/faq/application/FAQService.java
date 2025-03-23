package com.capstone.favicon.faq.application;

import com.capstone.favicon.faq.domain.FAQ;
import com.capstone.favicon.faq.dto.FAQRequestDto;
import com.capstone.favicon.faq.repository.FAQRepository;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class FAQService {

    private final FAQRepository faqRepository;
    private final UserRepository userRepository;

    public FAQService(FAQRepository faqRepository, UserRepository userRepository) {
        this.faqRepository = faqRepository;
        this.userRepository = userRepository;
    }

    public void createFAQ(Long userId, FAQRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        FAQ faq = new FAQ();
        faq.setUser(user);  // ✅ User 객체 저장
        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());

        faqRepository.save(faq);
    }

    public void updateFAQ(Long faqId, FAQRequestDto request) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));

        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());

        faqRepository.save(faq);
    }

    public void deleteFAQ(Long faqId) {
        FAQ faq = faqRepository.findById(faqId).orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        faqRepository.delete(faq);
    }
}