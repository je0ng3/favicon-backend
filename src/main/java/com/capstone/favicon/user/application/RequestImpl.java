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
import com.capstone.favicon.config.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            throw new ResourceNotFoundException("유저 아이디를 찾을 수 없음: " + dataRequestDto.getUserId());
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
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾지 못했습니다"));

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
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("답변을 찾을 수 없습니다"));

        answer.setContent(updatedAnswer.getContent());
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepository.deleteById(answerId);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestStatsDto getRequestStats() {
        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1);

        // 최근 6개월 요청/대기 건수를 DB에서 (연,월)별로 집계한다.
        Map<String, Integer> monthlyCounts = toMonthlyMap(dataRequestRepository.countMonthlySince(sixMonthsAgo));
        Map<String, Integer> monthlyPendingCounts = toMonthlyMap(
                dataRequestRepository.countMonthlyByStatusSince(sixMonthsAgo, DataRequest.ReviewStatus.PENDING));

        Map<String, Integer> monthlyCumulativeCounts = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>();
        int cumulativeSum = 0;
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i).withDayOfMonth(1);
            String key = monthKey(month.getYear(), month.getMonthValue());
            keys.add(key);
            cumulativeSum += monthlyCounts.getOrDefault(key, 0);
            monthlyCumulativeCounts.put(key, cumulativeSum);
        }

        String currentKey = keys.get(keys.size() - 1);
        String previousKey = keys.size() >= 2 ? keys.get(keys.size() - 2) : null;

        int currentMonthTotal = monthlyCounts.getOrDefault(currentKey, 0);
        int previousMonthTotal = previousKey != null ? monthlyCounts.getOrDefault(previousKey, 0) : 0;
        int growthFromLastMonth = growthRate(currentMonthTotal, previousMonthTotal);

        int currentPending = (int) dataRequestRepository.countByReviewStatus(DataRequest.ReviewStatus.PENDING);

        int currentMonthPending = monthlyPendingCounts.getOrDefault(currentKey, 0);
        int previousMonthPending = previousKey != null ? monthlyPendingCounts.getOrDefault(previousKey, 0) : 0;
        int pendingGrowthFromLastMonth = growthRate(currentMonthPending, previousMonthPending);

        return new RequestStatsDto(
                currentMonthTotal,
                growthFromLastMonth,
                currentPending,
                pendingGrowthFromLastMonth,
                monthlyCumulativeCounts
        );
    }

    /** [year, month, count] 행 목록을 "yyyy-MM" -> count 맵으로 변환한다. */
    private Map<String, Integer> toMonthlyMap(List<Object[]> rows) {
        Map<String, Integer> map = new HashMap<>();
        for (Object[] row : rows) {
            String key = monthKey(((Number) row[0]).intValue(), ((Number) row[1]).intValue());
            map.put(key, ((Number) row[2]).intValue());
        }
        return map;
    }

    private String monthKey(int year, int month) {
        return String.format("%04d-%02d", year, month);
    }

    private int growthRate(int current, int previous) {
        return previous > 0
                ? (int) Math.round(((double) (current - previous) / previous) * 100)
                : 0;
    }




    @Override
    public String getFileUrlByRequestId(Long requestId) {
        DataRequest dataRequest = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 요청이 존재하지 않습니다: " + requestId));
        return dataRequest.getFileUrl();
    }

    @Override
    public FileExtension getFileExtensionByRequestId(Long requestId) {
        DataRequest dataRequest = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 요청이 존재하지 않습니다: " + requestId));
        return extractExtension(dataRequest.getFileUrl());
    }

    private FileExtension extractExtension(String fileUrl) {
        String ext = fileUrl.substring(fileUrl.lastIndexOf('.') + 1).toUpperCase();
        return FileExtension.valueOf(ext);
    }


}