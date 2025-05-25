package com.capstone.favicon.user.controller;

import com.capstone.favicon.user.application.service.DataService;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.application.service.RequestService;
import com.capstone.favicon.user.dto.RequestStatsDto;
import com.capstone.favicon.user.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/list")
    public ResponseEntity<List<DataRequest>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @PostMapping(value = "/list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataRequest> createRequest(
            @RequestPart("dataRequestDto") DataRequestDto dataRequestDto,
            @RequestPart("file") MultipartFile file) {

        dataRequestDto.setFile(file);
        return ResponseEntity.ok(requestService.createRequest(dataRequestDto));
    }

    @PutMapping("/list/{requestId}/review")
    public ResponseEntity<DataRequest> updateReviewStatus(@PathVariable Long requestId, @RequestParam DataRequest.ReviewStatus status) {
        return ResponseEntity.ok(requestService.updateReviewStatus(requestId, status));
    }

    @GetMapping("/stats")
    public ResponseEntity<RequestStatsDto> getRequestStats() {
        return ResponseEntity.ok(requestService.getRequestStats());
    }

    @GetMapping("/question")
    public ResponseEntity<List<Question>> getQuestions(@RequestParam Long userId) {
        return ResponseEntity.ok(requestService.getQuestionsByUser(userId));
    }

    @GetMapping("/answer")
    public ResponseEntity<List<Answer>> getAnswers(@RequestParam Long questionId) {
        return ResponseEntity.ok(requestService.getAnswersByQuestion(questionId));
    }

    // 요청 게시글 수정
    @PutMapping("/{requestId}")
    public ResponseEntity<DataRequest> updateRequest(@PathVariable Long requestId, @RequestBody DataRequest updatedRequest) {
        return ResponseEntity.ok(requestService.updateRequest(requestId, updatedRequest));
    }

    // 요청 게시글 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    // 질문 작성
    @PostMapping("/question")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        return ResponseEntity.ok(requestService.createQuestion(question));
    }

    // 질문 수정
    @PutMapping("/question/{questionId}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId, @RequestBody Question updatedQuestion) {
        return ResponseEntity.ok(requestService.updateQuestion(questionId, updatedQuestion));
    }

    // 질문 삭제
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        requestService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // 답변 작성
    @PostMapping("/answer")
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        return ResponseEntity.ok(requestService.createAnswer(answer));
    }

    // 답변 수정
    @PutMapping("/answer/{answerId}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable Long answerId, @RequestBody Answer updatedAnswer) {
        return ResponseEntity.ok(requestService.updateAnswer(answerId, updatedAnswer));
    }

    // 답변 삭제
    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        requestService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }

}