package com.capstone.favicon.user.application.service;

import com.capstone.favicon.dataset.domain.FileExtension;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.AnswerRequestDto;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.dto.DataRequestUpdateDto;
import com.capstone.favicon.user.dto.QuestionRequestDto;
import com.capstone.favicon.user.dto.RequestStatsDto;

import java.util.List;

public interface RequestService {
    RequestStatsDto getRequestStats();
    List<DataRequest> getAllRequests();
    DataRequest createRequest(User author, DataRequestDto dataRequestDto);
    DataRequest updateReviewStatus(Long requestId, DataRequest.ReviewStatus status);
    List<Question> getQuestionsByUser(Long userId);
    List<Answer> getAnswersByQuestion(Long questionId);

    // 수정·삭제는 작성자 본인 또는 관리자만 가능하므로 호출자(actor)를 함께 받는다
    DataRequest updateRequest(Long requestId, DataRequestUpdateDto updatedRequest, User actor);
    void deleteRequest(Long requestId, User actor);

    Question createQuestion(User author, QuestionRequestDto request);
    Question updateQuestion(Long questionId, QuestionRequestDto request, User actor);
    void deleteQuestion(Long questionId, User actor);

    Answer createAnswer(User author, AnswerRequestDto request);
    Answer updateAnswer(Long answerId, AnswerRequestDto request, User actor);
    void deleteAnswer(Long answerId, User actor);


    String getFileUrlByRequestId(Long requestId);
    FileExtension getFileExtensionByRequestId(Long requestId);
}