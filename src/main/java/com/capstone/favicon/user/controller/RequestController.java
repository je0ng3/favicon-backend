package com.capstone.favicon.user.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.dataset.application.service.S3FileDownloadService;
import org.springframework.core.io.Resource;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.dto.DataRequestDto;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.application.service.RequestService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.AnswerRequestDto;
import com.capstone.favicon.user.dto.DataRequestUpdateDto;
import com.capstone.favicon.user.dto.QuestionRequestDto;
import com.capstone.favicon.user.dto.RequestStatsDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.capstone.favicon.user.dto.AnswerResponseDto;
import com.capstone.favicon.user.dto.DataRequestResponseDto;
import com.capstone.favicon.user.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final S3FileDownloadService s3FileDownloadService;

    @GetMapping("/list")
    public ResponseEntity<APIResponse<?>> getAllRequests() {
        List<DataRequest> requests = requestService.getAllRequests();
        return ResponseEntity.ok().body(APIResponse.successAPI("Success",
                requests.stream().map(DataRequestResponseDto::from).toList()));
    }

    @PostMapping(value = "/list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<?>> createRequest(
            @RequestPart("dataRequestDto") DataRequestDto dataRequestDto,
            @RequestPart("file") MultipartFile file) {
        dataRequestDto.setFile(file);
        DataRequest created = requestService.createRequest(dataRequestDto);
        return ResponseEntity.ok().body(APIResponse.successAPI("success", DataRequestResponseDto.from(created)));
    }

    @PutMapping("/list/{requestId}/review")
    public ResponseEntity<APIResponse<?>> updateReviewStatus(@PathVariable Long requestId, @RequestParam DataRequest.ReviewStatus status) {
        DataRequest dataRequest = requestService.updateReviewStatus(requestId, status);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", DataRequestResponseDto.from(dataRequest)));
    }

    @GetMapping("/stats")
    public ResponseEntity<APIResponse<?>> getRequestStats() {
        RequestStatsDto stats = requestService.getRequestStats();
        return ResponseEntity.ok().body(APIResponse.successAPI("success", stats));
    }

    @GetMapping("/question")
    public ResponseEntity<APIResponse<?>> getQuestions(@RequestParam Long userId) {
        List<Question> questions = requestService.getQuestionsByUser(userId);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success",
                questions.stream().map(QuestionResponseDto::from).toList()));
    }

    @GetMapping("/answer")
    public ResponseEntity<APIResponse<?>> getAnswers(@RequestParam Long questionId) {
        List<Answer> answers = requestService.getAnswersByQuestion(questionId);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success",
                answers.stream().map(AnswerResponseDto::from).toList()));
    }

    // 요청 게시글 수정
    @PutMapping("/{requestId}")
    public ResponseEntity<APIResponse<?>> updateRequest(@PathVariable Long requestId, @RequestBody DataRequestUpdateDto updatedRequest) {
        DataRequest dataRequest = requestService.updateRequest(requestId, updatedRequest);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", DataRequestResponseDto.from(dataRequest)));
    }

    // 요청 게시글 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<APIResponse<?>> deleteRequest(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    // 질문 작성
    @PostMapping("/question")
    public ResponseEntity<APIResponse<?>> createQuestion(@RequestBody QuestionRequestDto request,
                                                        @AuthenticationPrincipal User author) {
        Question newQuestion = requestService.createQuestion(author, request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", QuestionResponseDto.from(newQuestion)));
    }

    // 질문 수정
    @PutMapping("/question/{questionId}")
    public ResponseEntity<APIResponse<?>> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequestDto request) {
        Question newQuestion = requestService.updateQuestion(questionId, request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", QuestionResponseDto.from(newQuestion)));
    }

    // 질문 삭제
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<APIResponse<?>> deleteQuestion(@PathVariable Long questionId) {
        requestService.deleteQuestion(questionId);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", null));
    }

    // 답변 작성
    @PostMapping("/answer")
    public ResponseEntity<APIResponse<?>> createAnswer(@RequestBody AnswerRequestDto request,
                                                      @AuthenticationPrincipal User author) {
        Answer newAnswer = requestService.createAnswer(author, request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", AnswerResponseDto.from(newAnswer)));
    }

    // 답변 수정
    @PutMapping("/answer/{answerId}")
    public ResponseEntity<APIResponse<?>> updateAnswer(@PathVariable Long answerId, @RequestBody AnswerRequestDto request) {
        Answer newAnswer = requestService.updateAnswer(answerId, request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", AnswerResponseDto.from(newAnswer)));
    }

    // 답변 삭제
    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<APIResponse<?>> deleteAnswer(@PathVariable Long answerId) {
        requestService.deleteAnswer(answerId);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", null));
    }

    @GetMapping("/download/{requestId}")
    public ResponseEntity<Resource> downloadDataRequestFile(@PathVariable Long requestId) throws IOException {
        File downloadedFile = s3FileDownloadService.downloadFileFromDataRequest(requestId);
        Resource fileResource = new FileSystemResource(downloadedFile);
        String fileName = downloadedFile.getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }

}
