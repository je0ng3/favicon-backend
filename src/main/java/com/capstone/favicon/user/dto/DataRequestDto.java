package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DataRequestDto {
    private Long userId;
    private String purpose;
    private String title;
    private String content;
    private MultipartFile file;
    private String fileUrl;
    private String organization;
}