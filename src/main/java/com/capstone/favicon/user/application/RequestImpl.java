package com.capstone.favicon.user.application;

import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.repository.UserRepository;
import com.capstone.favicon.user.repository.DataRequestRepository;
import com.capstone.favicon.user.repository.QuestionRepository;
import com.capstone.favicon.user.repository.AnswerRepository;
import com.capstone.favicon.user.application.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestImpl implements RequestService {
    private final DataRequestRepository dataRequestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    public List<DataRequest> getAllRequests() {
        return dataRequestRepository.findAll();
    }

    @Override
    @Transactional
    public DataRequest createRequest(DataRequestDto dataRequestDto) {
        User user = userRepository.findByUserId(dataRequestDto.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found with userId: " + dataRequestDto.getUserId());
        }

        DataRequest dataRequest = new DataRequest();
        dataRequest.setUser(user);
        dataRequest.setPurpose(dataRequestDto.getPurpose());
        dataRequest.setTitle(dataRequestDto.getTitle());
        dataRequest.setContent(dataRequestDto.getContent());
        dataRequest.setUploadDate(LocalDate.now());
        dataRequest.setFileUrl(dataRequestDto.getFileUrl());
        dataRequest.setOrganization(dataRequestDto.getOrganization());
        dataRequest.setReviewStatus(DataRequest.ReviewStatus.PENDING);

        return dataRequestRepository.save(dataRequest);
    }

    @Override
    @Transactional
    public DataRequest updateReviewStatus(Long requestId, DataRequest.ReviewStatus status) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setReviewStatus(status);
        return dataRequestRepository.save(request);
    }

    @Override
    public List<Question> getQuestionsByUser(Long userId) {
        return questionRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Answer> getAnswersByQuestion(Long questionId) {
        return answerRepository.findByQuestion_User_UserId(questionId);
    }

    // --- ✨ 추가 기능들 ---

    @Override
    @Transactional
    public DataRequest updateRequest(Long requestId, DataRequest updatedRequest) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setPurpose(updatedRequest.getPurpose());
        request.setTitle(updatedRequest.getTitle());
        request.setContent(updatedRequest.getContent());
        request.setFileUrl(updatedRequest.getFileUrl());
        request.setOrganization(updatedRequest.getOrganization());
        return dataRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void deleteRequest(Long requestId) {
        dataRequestRepository.deleteById(requestId);
    }

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question updateQuestion(Long questionId, Question updatedQuestion) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setContent(updatedQuestion.getContent());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    @Override
    @Transactional
    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public Answer updateAnswer(Long answerId, Answer updatedAnswer) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        answer.setContent(updatedAnswer.getContent());
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepository.deleteById(answerId);
    }
}