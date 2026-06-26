package com.capstone.favicon.config.exception;

import com.capstone.favicon.config.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 컨트롤러의 공통 예외 처리. 컨트롤러에서 try/catch 로 예외를 잡아 직접 응답을 만들 필요 없이
 * 여기서 예외 타입에 맞는 HTTP 상태 코드와 APIResponse 를 일관되게 반환한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 리소스를 찾을 수 없음 → 404 */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<?>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.errorAPI(e.getMessage()));
    }

    /** 잘못된 요청(검증 실패, 형식 오류 등) → 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<?>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
    }

    /** 인증 실패(비밀번호 불일치, refresh 토큰 만료 등) → 401 */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<?>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.errorAPI(e.getMessage()));
    }

    /** @Valid 바디 검증 실패 → 400 (첫 번째 필드 오류 메시지 반환) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
    }

    /** 그 외 예상치 못한 예외 → 500 (상세 내용은 로그로만 남기고 클라이언트에는 노출하지 않음) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.errorAPI("서버 내부 오류가 발생했습니다."));
    }
}
