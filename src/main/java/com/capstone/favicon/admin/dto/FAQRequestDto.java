package com.capstone.favicon.admin.dto;

import com.capstone.favicon.admin.domain.FAQ.FAQCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FAQRequestDto {
    private FAQCategory category;
    private String question;
    private String answer;
}