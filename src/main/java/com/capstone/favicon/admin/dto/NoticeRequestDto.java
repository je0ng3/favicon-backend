package com.capstone.favicon.admin.dto;

import com.capstone.favicon.admin.domain.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {
    private String title;
    private String content;
    private Notice.NoticeLabel label = Notice.NoticeLabel.일반;
}
