package com.capstone.favicon.notice.controller;

import com.capstone.favicon.notice.domain.Notice;
import com.capstone.favicon.notice.dto.NoticeRequestDto;
import com.capstone.favicon.notice.application.NoticeServiceImpl;
import com.capstone.favicon.notice.dto.NoticeResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeServiceImpl noticeService;

    @PostMapping("/create")
    public ResponseEntity<?> createNotice(@RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        noticeService.createNotice(noticeService.getAdminUserFromSession(httpRequest).getUserId(), request);
        return ResponseEntity.ok("공지사항이 등록되었습니다.");
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        noticeService.getAdminUserFromSession(httpRequest);
        noticeService.updateNotice(noticeId, request);
        return ResponseEntity.ok("공지사항이 수정되었습니다.");
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId, HttpServletRequest httpRequest) {
        noticeService.getAdminUserFromSession(httpRequest);
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeById(noticeId));
    }

    @GetMapping("/view/{noticeId}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }
}
