package com.capstone.favicon.admin.application.service;

import com.capstone.favicon.admin.domain.Notice;
import com.capstone.favicon.admin.dto.NoticeRequestDto;
import com.capstone.favicon.admin.dto.NoticeResponseDto;
import com.capstone.favicon.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface NoticeService {
    User getAdminUserFromSession(HttpServletRequest request);

    void createNotice(Long userId, NoticeRequestDto request);
    void updateNotice(Long noticeId, NoticeRequestDto request);
    void deleteNotice(Long noticeId);
    List<NoticeResponseDto> getAllNotices();
    NoticeResponseDto getNoticeById(Long noticeId);
    Notice getNotice(Long noticeId);
}