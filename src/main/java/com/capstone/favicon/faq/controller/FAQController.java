package com.capstone.favicon.faq.controller;

import com.capstone.favicon.faq.dto.FAQRequestDto;
import com.capstone.favicon.faq.application.FAQService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;
    private final UserRepository userRepository;

    private User getAdminUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user == null || user.getRole() != 1) {
            throw new RuntimeException("이 기능은 관리자만 접근 가능합니다.");
        }
        return user;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFAQ(@RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 생성했습니다.");
        faqService.createFAQ(admin.getUserId(), request);
        return ResponseEntity.ok("자주묻는질문이 생성되었습니다.");
    }

    @PutMapping("/{faqId}")
    public ResponseEntity<?> updateFAQ(@PathVariable Long faqId, @RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 수정했습니다.");
        faqService.updateFAQ(faqId, request);
        return ResponseEntity.ok("자주묻는질문이 수정되었습니다.");
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<?> deleteFAQ(@PathVariable Long faqId, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 FAQ를 삭제했습니다.");
        faqService.deleteFAQ(faqId);
        return ResponseEntity.ok("자주묻는질문이 삭제되었습니다.");
    }
}