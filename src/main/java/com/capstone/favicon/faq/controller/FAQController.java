package com.capstone.favicon.faq.controller;

import com.capstone.favicon.faq.dto.FAQRequestDto;
import com.capstone.favicon.faq.application.FAQServiceImpl;
import com.capstone.favicon.faq.dto.FAQResponseDto;
import com.capstone.favicon.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FAQController {

    private final FAQServiceImpl faqService;

    @PostMapping("/create")
    public ResponseEntity<?> createFAQ(@RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        User admin = faqService.getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 생성했습니다.");
        faqService.createFAQ(admin.getUserId(), request);
        return ResponseEntity.ok("자주묻는질문이 생성되었습니다.");
    }

    @PutMapping("/{faqId}")
    public ResponseEntity<?> updateFAQ(@PathVariable Long faqId, @RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        User admin = faqService.getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 수정했습니다.");
        faqService.updateFAQ(faqId, request);
        return ResponseEntity.ok("자주묻는질문이 수정되었습니다.");
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<?> deleteFAQ(@PathVariable Long faqId, HttpServletRequest httpRequest) {
        User admin = faqService.getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 삭제했습니다.");
        faqService.deleteFAQ(faqId);
        return ResponseEntity.ok("자주묻는질문이 삭제되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FAQResponseDto>> getAllFAQs() {
        List<FAQResponseDto> faqs = faqService.getAllFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<FAQResponseDto> getFAQById(@PathVariable Long faqId) {
        FAQResponseDto faq = faqService.getFAQById(faqId);
        return ResponseEntity.ok(faq);
    }

}
