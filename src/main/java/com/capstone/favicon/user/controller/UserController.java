package com.capstone.favicon.user.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.security.SessionAuthenticator;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users/auth")
public class UserController {

    private final UserService userService;
    private final SessionAuthenticator sessionAuthenticator;

    @PostMapping("/email-check")
    public ResponseEntity<APIResponse<?>> emailCheck(@RequestBody RegisterDto.checkEmail checkEmail) {
        userService.sendCode(checkEmail);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", checkEmail.getEmail()));
    }

    @PostMapping("/code-check")
    public ResponseEntity<APIResponse<?>> checkCode(@RequestBody RegisterDto.checkCode checkCode) {
        userService.checkCode(checkCode);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", checkCode.getCode()));
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<?>> register(@RequestBody RegisterDto registerDto) {
        userService.join(registerDto);
        return ResponseEntity.ok().body(APIResponse.successAPI("Success", registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto,
                                                HttpServletRequest request, HttpServletResponse response) {
        User user = userService.login(loginDto);
        String sessionId = sessionAuthenticator.startSession(user, request, response);
        LoginResponseDto responseDto = new LoginResponseDto(user.getUserId(), user.getUsername(), sessionId);
        return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", responseDto));
    }

    /** 세션 만료 시각은 요청이 오는 것만으로 연장된다. 여기서는 현재 세션을 확인해 돌려줄 뿐이다. */
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<?>> refresh(HttpServletRequest request,
                                                  @AuthenticationPrincipal User user) {
        HttpSession session = request.getSession(false);
        if (session == null || user == null) {
            throw new BadCredentialsException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }
        LoginResponseDto responseDto = new LoginResponseDto(user.getUserId(), user.getUsername(), session.getId());
        return ResponseEntity.ok().body(APIResponse.successAPI("Session refreshed.", responseDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<?>> logout(HttpServletRequest request) {
        sessionAuthenticator.endSession(request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Successfully logout.", null));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<?>> deleteUser(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.delete(user);
        // 호출자 본인 세션은 요청 종료 시 저장소에 다시 쓰이므로 여기서 명시적으로 끊는다
        sessionAuthenticator.endSession(request);
        return ResponseEntity.ok().body(APIResponse.successAPI("탈퇴하였습니다.", null));
    }

}
