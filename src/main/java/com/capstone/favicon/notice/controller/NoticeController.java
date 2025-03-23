package com.capstone.favicon.notice.controller;

import com.capstone.favicon.notice.domain.Notice;
import com.capstone.favicon.notice.dto.NoticeRequestDto;
import com.capstone.favicon.notice.application.NoticeService;
import com.capstone.favicon.notice.dto.NoticeResponseDto;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserRepository userRepository;

    private User getAdminUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException("세션이 존재하지 않습니다.");
        }

        String email = (String) session.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("세션에 이메일 정보가 없습니다.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null || user.getRole() != 1) {
            throw new RuntimeException("이 기능은 관리자만 접근 가능합니다.");
        }
        return user;
    }

    /* private User getAdminUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user == null || user.getRole() != 1) {
            throw new RuntimeException("관리자만 접근 가능합니다.");
        }
        return user;
    } */

    @PostMapping("/create")
    public ResponseEntity<?> createNotice(@RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 공지를 생성했습니다.");
        noticeService.createNotice(admin.getUserId(), request);
        return ResponseEntity.ok("공지사항이 등록되었습니다.");
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 공지를 수정했습니다.");
        noticeService.updateNotice(noticeId, request);
        return ResponseEntity.ok("공지사항이 수정되었습니다.");
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId, HttpServletRequest httpRequest) {
        User admin = getAdminUserFromSession(httpRequest);
        System.out.println("관리자 : " + admin.getUserId() + " 가 공지를 삭제했습니다.");
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        List<NoticeResponseDto> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable Long noticeId) {
        NoticeResponseDto notice = noticeService.getNoticeById(noticeId);
        return ResponseEntity.ok(notice);
    }

    @GetMapping("/view/{noticeId}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(notice);
    }

}