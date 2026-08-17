package com.capstone.favicon.admin.dto;

import com.capstone.favicon.admin.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private String createDate;
    private String updateDate;
    private int view;
    private String label;

    public static NoticeResponseDto from(Notice notice) {
        return new NoticeResponseDto(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreateDate().toString(),
                notice.getUpdateDate() == null ? null : notice.getUpdateDate().toString(),
                notice.getView(),
                notice.getLabel().name()
        );
    }
}