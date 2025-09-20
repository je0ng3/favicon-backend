package com.capstone.favicon.admin.application.service;

import com.capstone.favicon.admin.domain.Notice;
import com.capstone.favicon.admin.dto.NoticeRequestDto;
import com.capstone.favicon.admin.dto.NoticeResponseDto;

import java.util.List;

public interface NoticeService {
    void createNotice(NoticeRequestDto request);
    void updateNotice(Long noticeId, NoticeRequestDto request);
    void deleteNotice(Long noticeId);
    List<NoticeResponseDto> getAllNotices();
    NoticeResponseDto getNoticeById(Long noticeId);
    Notice getNotice(Long noticeId);
}