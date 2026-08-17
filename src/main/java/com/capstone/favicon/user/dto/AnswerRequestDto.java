package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 작성자와 작성일은 서버가 정한다. questionId 는 작성 시에만 쓰이고 수정 시에는 무시된다. */
@Getter
@Setter
@NoArgsConstructor
public class AnswerRequestDto {

    private Long questionId;
    private String content;
}
