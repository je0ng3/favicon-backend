package com.capstone.favicon.user.application;

import com.capstone.favicon.infrastructure.s3.S3Storage;
import com.capstone.favicon.dataset.domain.FileExtension;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.domain.User;
import org.springframework.security.access.AccessDeniedException;
import com.capstone.favicon.user.dto.AnswerRequestDto;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.dto.DataRequestUpdateDto;
import com.capstone.favicon.user.dto.QuestionRequestDto;
import com.capstone.favicon.user.dto.RequestStatsDto;
import com.capstone.favicon.user.repository.DataRequestRepository;
import com.capstone.favicon.user.repository.QuestionRepository;
import com.capstone.favicon.user.repository.AnswerRepository;
import com.capstone.favicon.user.application.service.RequestService;
import com.capstone.favicon.config.exception.ResourceNotFoundException;
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
public class RequestServiceImpl implements RequestService {
    private final DataRequestRepository dataRequestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final S3Storage s3Storage;

    public RequestServiceImpl(DataRequestRepository dataRequestRepository,QuestionRepository questionRepository,
                       AnswerRepository answerRepository,
                       S3Storage s3Storage) {
        this.dataRequestRepository = dataRequestRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.s3Storage = s3Storage;
    }

    @Override
    public List<DataRequest> getAllRequests() {
        return dataRequestRepository.findAll();
    }

    @Override
    @Transactional
    public DataRequest createRequest(User author, DataRequestDto dataRequestDto) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.setUser(author);
        dataRequest.setPurpose(dataRequestDto.getPurpose());
        dataRequest.setTitle(dataRequestDto.getTitle());
        dataRequest.setContent(dataRequestDto.getContent());
        dataRequest.setUploadDate(LocalDate.now());
        dataRequest.setOrganization(dataRequestDto.getOrganization());
        dataRequest.setReviewStatus(DataRequest.ReviewStatus.PENDING);

        // 키를 요청 ID 로 나눠 둬야 같은 이름의 파일을 올린 다른 요청과 한 객체를 공유하지 않는다
        // (공유하면 한쪽 요청을 지울 때 다른 쪽 파일까지 사라진다). ID 가 필요하므로 먼저 저장한다.
        DataRequest saved = dataRequestRepository.save(dataRequest);
        try {
            saved.setFileUrl(s3Storage.uploadFile(dataRequestDto.getFile(), "pending/" + saved.getDataRequestId()));
        } catch (IOException e) {
            throw new RuntimeException("s3에 업로드 실패", e);
        }
        return dataRequestRepository.save(saved);
    }

    @Override
    @Transactional
    public DataRequest updateReviewStatus(Long requestId, DataRequest.ReviewStatus status, User reviewer) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾지 못했습니다"));
        // 심사가 끝난 요청을 다시 심사하면 preprocessing/ 으로 옮겨 둔 파일을 지우거나 자기 자신에 copy 하게 된다
        if (request.getReviewStatus() != DataRequest.ReviewStatus.PENDING) {
            throw new IllegalArgumentException("이미 심사가 끝난 요청입니다.");
        }

        String fileUrl = request.getFileUrl();
        if (fileUrl != null) {
            String key = s3Storage.extractKeyFromAnyUrl(fileUrl);
            System.out.println("추출된 키: " + key);
            System.out.println("추출된 파일명: " + s3Storage.extractFileNameFromKey(key));

            if (status == DataRequest.ReviewStatus.APPROVED) {
                // 승인시 preprocessing 폴더로 이동. 여기서도 요청 ID 로 나눠야 같은 이름의 다른 승인 파일을 덮어쓰지 않는다
                String newKey = "preprocessing/" + requestId + "/" + s3Storage.extractFileNameFromKey(key);
                s3Storage.moveFile(key, newKey);
                request.setFileUrl(s3Storage.generateFileUrl(newKey));

            } else if (status == DataRequest.ReviewStatus.REJECTED) {
                // 거절시 pending 폴더에 있는 파일 삭제(테스트 완료)
                s3Storage.deleteFileByKey(key);
                request.setFileUrl(null);
            }
        }
        request.setReviewStatus(status);
        request.setReviewedBy(reviewer);
        request.setReviewDate(LocalDate.now());
        return dataRequestRepository.save(request);
    }

    @Override
    public List<Question> getQuestionsByUser(Long userId) {
        return questionRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Answer> getAnswersByQuestion(Long questionId) {
        return answerRepository.findByQuestion_QuestionId(questionId);
    }


    @Override
    @Transactional
    public DataRequest updateRequest(Long requestId, DataRequestUpdateDto updatedRequest, User actor) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다"));
        assertCanModify(actor, request.getUser());

        // 부분 수정 바디로 나머지 필드가 null 로 지워지지 않도록 넘어온 값만 반영한다
        if (updatedRequest.getPurpose() != null) request.setPurpose(updatedRequest.getPurpose());
        if (updatedRequest.getTitle() != null) request.setTitle(updatedRequest.getTitle());
        if (updatedRequest.getContent() != null) request.setContent(updatedRequest.getContent());
        if (updatedRequest.getOrganization() != null) request.setOrganization(updatedRequest.getOrganization());
        return dataRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void deleteRequest(Long requestId, User actor) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다"));
        assertCanModify(actor, request.getUser());
        // DB 행만 지우면 pending/ 에 올라간 파일이 참조 없이 남는다. 반대로 승인된 요청의 파일은
        // preprocessing/ 으로 옮겨져 데이터셋이 참조하므로 글만 지우고 파일은 건드리지 않는다.
        if (request.getReviewStatus() == DataRequest.ReviewStatus.PENDING && request.getFileUrl() != null) {
            s3Storage.deleteFileByKey(s3Storage.extractKeyFromAnyUrl(request.getFileUrl()));
        }
        dataRequestRepository.delete(request);
    }

    @Override
    @Transactional
    public Question createQuestion(User author, QuestionRequestDto request) {
        Question question = new Question();
        question.setUser(author);
        question.setContent(requireContent(request.getContent()));
        question.setCreateDate(LocalDate.now());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question updateQuestion(Long questionId, QuestionRequestDto request, User actor) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        assertCanModify(actor, question.getUser());

        question.setContent(requireContent(request.getContent()));
        return questionRepository.save(question);
    }

    /** 빈 본문이 그대로 저장되거나 기존 본문을 null 로 지우지 않도록 막는다(400). */
    private String requireContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        return content;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, User actor) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        assertCanModify(actor, question.getUser());
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public Answer createAnswer(User author, AnswerRequestDto request) {
        // 옛 바디 형태({"question":{"questionId":n}})로 오면 여기가 null 이다. findById(null) 은 500 이 되므로 400 으로 돌린다.
        if (request.getQuestionId() == null) {
            throw new IllegalArgumentException("questionId 가 필요합니다.");
        }
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(author);
        answer.setContent(requireContent(request.getContent()));
        answer.setCreateDate(LocalDate.now());
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public Answer updateAnswer(Long answerId, AnswerRequestDto request, User actor) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("답변을 찾을 수 없습니다"));
        assertCanModify(actor, answer.getUser());

        answer.setContent(requireContent(request.getContent()));
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId, User actor) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("답변을 찾을 수 없습니다"));
        assertCanModify(actor, answer.getUser());
        answerRepository.delete(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyRequestAccess(Long requestId, User actor) {
        DataRequest request = dataRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("요청을 찾을 수 없습니다"));
        assertCanModify(actor, request.getUser());
    }

    /** 작성자 본인 또는 관리자만 수정·삭제할 수 있다. */
    private void assertCanModify(User actor, User owner) {
        if (actor == null || owner == null || !(actor.isAdmin() || owner.getUserId().equals(actor.getUserId()))) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
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