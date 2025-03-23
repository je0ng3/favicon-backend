package com.capstone.favicon.faq.dto;

import com.capstone.favicon.faq.domain.FAQ.FAQCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FAQRequestDto {
    private FAQCategory category;
    private String question;
    private String answer;
}