package com.capstone.favicon.user.application;

import com.capstone.favicon.config.S3Config;
import com.capstone.favicon.dataset.domain.FileExtension;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.dto.RequestStatsDto;
import com.capstone.favicon.user.repository.UserRepository;
import com.capstone.favicon.user.repository.DataRequestRepository;
import com.capstone.favicon.user.repository.QuestionRepository;
import com.capstone.favicon.user.repository.AnswerRepository;
import com.capstone.favicon.user.application.service.RequestService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;

@Service
public class RequestImpl implements RequestService {
    private final DataRequestRepository dataRequestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final S3Config s3Config;

    public RequestImpl(DataRequestRepository dataRequestRepository,QuestionRepository questionRepository,
                       AnswerRepository answerRepository, UserRepository userRepository,
                       @Qualifier("s3Config") S3Config s3Config) {
        this.dataRequestRepository = dataRequestRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.s3Config = s3Config;
    }

    @Override
    public List<DataRequest> getAllRequests() {
        return dataRequestRepository.findAll();
    }

    @Override
    @Transactional
    public DataRequest createRequest(DataRequestDto dataRequestDto) {
        User user = userRepository.findByUserId(dataRequestDto.getUserId());
        if (user == null) {
            throw new RuntimeException("유저 아이디를 찾을 수 없음: " + dataRequestDto.getUserId());
        }

        DataRequest dataRequest = new DataRequest();
        dataRequest.setUser(user);
        dataRequest.setPurpose(dataRequestDto.getPurpose());
        dataRequest.setTitle(dataRequestDto.getTitle());
        dataRequest.setContent(dataRequestDto.getContent());
        dataRequest.setUploadDate(LocalDate.now());
        try {
            String uploadedUrl = s3Config.uploadFile(dataRequestDto.getFile(), "pending");
            dataRequest.setFileUrl(uploadedUrl);
        } catch (IOException e) {
            throw new RuntimeException("s3에 업로드 실패", e);
        }
        //dataRequest.setFileUrl(dataRequestDto.getFileUrl());
        dataRequest.setOrganization(dataRequestDto.getOrganization());
        dataRequest.setReviewStatus(DataRequest.ReviewStatus.PENDING);

        return dataRequestRepository.save(dataRequest);
    }

    @Override
    @Transactional
    public DataRequest updateReviewStatus(Long requestId, DataRequest.ReviewStatus status) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("요청을 찾지 못했습니다"));

        String fileUrl = request.getFileUrl();
        if (fileUrl != null) {
            String key = s3Config.extractKeyFromAnyUrl(fileUrl);
            System.out.println("추출된 키: " + key);
            System.out.println("추출된 파일명: " + s3Config.extractFileNameFromKey(key));

            if (status == DataRequest.ReviewStatus.APPROVED) {
                // 승인시 preprocessing 폴더로 이동(테스트 완료)
                String newKey = "preprocessing/" + s3Config.extractFileNameFromKey(key);
                s3Config.moveFile(key, newKey);
                request.setFileUrl(s3Config.generateFileUrl(newKey));

            } else if (status == DataRequest.ReviewStatus.REJECTED) {
                // 거절시 pending 폴더에 있는 파일 삭제(테스트 완료)
                s3Config.deleteFileByKey(key);
                request.setFileUrl(null);
            }
        }
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


    @Override
    @Transactional
    public DataRequest updateRequest(Long requestId, DataRequest updatedRequest) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("요청을 찾을 수 없습니다"));

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
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다"));

        answer.setContent(updatedAnswer.getContent());
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepository.deleteById(answerId);
    }

    @Override
    public RequestStatsDto getRequestStats() {
        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1);

        List<DataRequest> allRequests = dataRequestRepository.findAll();

        Map<String, Integer> monthlyCumulativeCounts = new LinkedHashMap<>();
        int cumulativeSum = 0;

        Map<String, Long> monthlyCounts = allRequests.stream()
                .filter(req -> !req.getUploadDate().isBefore(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        req -> req.getUploadDate().withDayOfMonth(1).toString().substring(0, 7),
                        TreeMap::new,
                        Collectors.counting()
                ));

        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i).withDayOfMonth(1);
            String key = month.toString().substring(0, 7);
            int monthly = monthlyCounts.getOrDefault(key, 0L).intValue();
            cumulativeSum += monthly;
            monthlyCumulativeCounts.put(key, cumulativeSum);
        }

        List<String> keys = new ArrayList<>(monthlyCumulativeCounts.keySet());

        int currentMonthTotal = monthlyCounts.getOrDefault(keys.get(keys.size() - 1), 0L).intValue();
        int previousMonthTotal = keys.size() >= 2 ? monthlyCounts.getOrDefault(keys.get(keys.size() - 2), 0L).intValue() : 0;
        int growthFromLastMonth = previousMonthTotal > 0
                ? (int) Math.round(((double)(currentMonthTotal - previousMonthTotal) / previousMonthTotal) * 100)
                : 0;

        int currentPending = (int) allRequests.stream()
                .filter(req -> req.getReviewStatus() == DataRequest.ReviewStatus.PENDING)
                .count();

        Map<String, Long> monthlyPendingCounts = allRequests.stream()
                .filter(req -> req.getReviewStatus() == DataRequest.ReviewStatus.PENDING)
                .filter(req -> !req.getUploadDate().isBefore(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        req -> req.getUploadDate().withDayOfMonth(1).toString().substring(0, 7),
                        TreeMap::new,
                        Collectors.counting()
                ));

        int currentMonthPending = monthlyPendingCounts.getOrDefault(keys.get(keys.size() - 1), 0L).intValue();
        int previousMonthPending = keys.size() >= 2 ? monthlyPendingCounts.getOrDefault(keys.get(keys.size() - 2), 0L).intValue() : 0;
        int pendingGrowthFromLastMonth = previousMonthPending > 0
                ? (int) Math.round(((double)(currentMonthPending - previousMonthPending) / previousMonthPending) * 100)
                : 0;

        return new RequestStatsDto(
                currentMonthTotal,
                growthFromLastMonth,
                currentPending,
                pendingGrowthFromLastMonth,
                monthlyCumulativeCounts
        );
    }




    @Override
    public String getFileUrlByRequestId(Long requestId) {
        DataRequest dataRequest = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 요청이 존재하지 않습니다: " + requestId));
        return dataRequest.getFileUrl();
    }

    @Override
    public FileExtension getFileExtensionByRequestId(Long requestId) {
        DataRequest dataRequest = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 요청이 존재하지 않습니다: " + requestId));
        return extractExtension(dataRequest.getFileUrl());
    }

    private FileExtension extractExtension(String fileUrl) {
        String ext = fileUrl.substring(fileUrl.lastIndexOf('.') + 1).toUpperCase();
        return FileExtension.valueOf(ext);
    }


}