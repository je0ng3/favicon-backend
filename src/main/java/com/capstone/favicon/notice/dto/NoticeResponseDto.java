package com.capstone.favicon.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private String createDate;
    private int view;
    private String label;
}