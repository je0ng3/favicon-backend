package com.capstone.favicon.user.dto;

import com.capstone.favicon.user.domain.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AnswerResponseDto {

    private Long answerId;
    private Long questionId;
    private Long userId;
    private String content;
    private LocalDate createDate;

    public static AnswerResponseDto from(Answer answer) {
        return new AnswerResponseDto(
                answer.getAnswerId(),
                answer.getQuestion() == null ? null : answer.getQuestion().getQuestionId(),
                answer.getUser() == null ? null : answer.getUser().getUserId(),
                answer.getContent(),
                answer.getCreateDate()
        );
    }
}
