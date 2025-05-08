package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.dto.DataRequestDto;

import java.util.List;

public interface RequestService {
    List<DataRequest> getAllRequests();
    DataRequest createRequest(DataRequestDto dataRequestDto);
    DataRequest updateReviewStatus(Long requestId, DataRequest.ReviewStatus status);
    List<Question> getQuestionsByUser(Long userId);
    List<Answer> getAnswersByQuestion(Long questionId);

    // ✨ 추가된 기능
    DataRequest updateRequest(Long requestId, DataRequest updatedRequest);
    void deleteRequest(Long requestId);

    Question createQuestion(Question question);
    Question updateQuestion(Long questionId, Question updatedQuestion);
    void deleteQuestion(Long questionId);

    Answer createAnswer(Answer answer);
    Answer updateAnswer(Long answerId, Answer updatedAnswer);
    void deleteAnswer(Long answerId);
}