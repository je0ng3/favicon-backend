package com.capstone.favicon.admin.controller;

import com.capstone.favicon.admin.application.service.NoticeService;
import com.capstone.favicon.admin.domain.Notice;
import com.capstone.favicon.admin.dto.NoticeRequestDto;
import com.capstone.favicon.admin.dto.NoticeResponseDto;
import com.capstone.favicon.config.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<?>> createNotice(@RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        try {
            noticeService.createNotice(noticeService.getAdminUserFromSession(httpRequest).getUserId(), request);
            return ResponseEntity.ok().body(APIResponse.successAPI("공지사항이 등록되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<APIResponse<?>> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto request, HttpServletRequest httpRequest) {
        try {
            noticeService.getAdminUserFromSession(httpRequest);
            noticeService.updateNotice(noticeId, request);
            return ResponseEntity.ok().body(APIResponse.successAPI("공지사항이 수정되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<APIResponse<?>> deleteNotice(@PathVariable Long noticeId, HttpServletRequest httpRequest) {
        try {
            noticeService.getAdminUserFromSession(httpRequest);
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok().body(APIResponse.successAPI("공지사항이 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse<?>> getAllNotices() {
        try {
            List<NoticeResponseDto> notices = noticeService.getAllNotices();
            return ResponseEntity.ok().body(APIResponse.successAPI("success", notices));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<APIResponse<?>> getNoticeById(@PathVariable Long noticeId) {
        try {
            NoticeResponseDto notice = noticeService.getNoticeById(noticeId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", notice));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @GetMapping("/view/{noticeId}")
    public ResponseEntity<APIResponse<?>> getNotice(@PathVariable Long noticeId) {
        try {
            Notice notice = noticeService.getNotice(noticeId);
            return ResponseEntity.ok().body(APIResponse.successAPI("success", notice));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }
}
