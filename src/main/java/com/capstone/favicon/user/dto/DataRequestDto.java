package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataRequestDto {
    private Long userId;
    private String purpose;
    private String title;
    private String content;
    private String fileUrl;
    private String organization;
}