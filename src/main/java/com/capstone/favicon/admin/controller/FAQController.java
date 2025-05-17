package com.capstone.favicon.admin.controller;

import com.capstone.favicon.admin.application.service.FAQService;
import com.capstone.favicon.admin.dto.FAQRequestDto;
import com.capstone.favicon.admin.dto.FAQResponseDto;
import com.capstone.favicon.config.APIResponse;
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

    private final FAQService faqService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<?>> createFAQ(@RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        try {
            User admin = faqService.getAdminUserFromSession(httpRequest);
            faqService.createFAQ(admin.getUserId(), request);
            return ResponseEntity.ok().body(APIResponse.successAPI("관리자 "+admin.getUserId()+"가 FAQ를 생성하였습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PutMapping("/{faqId}")
    public ResponseEntity<APIResponse<?>> updateFAQ(@PathVariable Long faqId, @RequestBody FAQRequestDto request, HttpServletRequest httpRequest) {
        try {
            User admin = faqService.getAdminUserFromSession(httpRequest);
            faqService.updateFAQ(faqId, request);
            return ResponseEntity.ok().body(APIResponse.successAPI("관리자 : " + admin.getUserId() + " 가 FAQ를 수정했습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<APIResponse<?>> deleteFAQ(@PathVariable Long faqId, HttpServletRequest httpRequest) {
        try {
            User admin = faqService.getAdminUserFromSession(httpRequest);
            faqService.deleteFAQ(faqId);
            return ResponseEntity.ok().body(APIResponse.successAPI("관리자 : " + admin.getUserId() + " 가 FAQ를 삭제했습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse<?>> getAllFAQs() {
        try {
            List<FAQResponseDto> faqs = faqService.getAllFAQs();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<APIResponse<?>> getFAQById(@PathVariable Long faqId) {
        try {
            FAQResponseDto faq = faqService.getFAQById(faqId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", faq));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

}
