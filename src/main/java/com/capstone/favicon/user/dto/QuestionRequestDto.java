package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 작성자와 작성일은 서버가 정한다. 엔티티를 그대로 받으면 다른 사용자 명의로 쓰거나 기존 글을 덮어쓸 수 있다. */
@Getter
@Setter
@NoArgsConstructor
public class QuestionRequestDto {

    private String content;
}
