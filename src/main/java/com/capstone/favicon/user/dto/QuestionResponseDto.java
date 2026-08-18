package com.capstone.favicon.user.dto;

import com.capstone.favicon.user.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class QuestionResponseDto {

    private Long questionId;
    private Long userId;
    private String content;
    private LocalDate createDate;

    public static QuestionResponseDto from(Question question) {
        return new QuestionResponseDto(
                question.getQuestionId(),
                question.getUser() == null ? null : question.getUser().getUserId(),
                question.getContent(),
                question.getCreateDate()
        );
    }
}
