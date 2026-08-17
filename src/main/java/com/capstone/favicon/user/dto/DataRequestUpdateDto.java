package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 수정 시 실제로 반영되는 필드만 받는다. 엔티티를 그대로 받으면 심사 상태나 작성자까지 요청 바디로 들어온다. */
@Getter
@Setter
@NoArgsConstructor
public class DataRequestUpdateDto {

    private String purpose;
    private String title;
    private String content;
    private String fileUrl;
    private String organization;
}
