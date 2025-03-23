package com.capstone.favicon.notice.application.service;

import com.capstone.favicon.notice.domain.Notice;
import com.capstone.favicon.notice.dto.NoticeRequestDto;
import com.capstone.favicon.notice.dto.NoticeResponseDto;

import java.util.List;

public interface NoticeService {
    void createNotice(Long userId, NoticeRequestDto request);
    void updateNotice(Long noticeId, NoticeRequestDto request);
    void deleteNotice(Long noticeId);
    List<NoticeResponseDto> getAllNotices();
    NoticeResponseDto getNoticeById(Long noticeId);
    Notice getNotice(Long noticeId);
}