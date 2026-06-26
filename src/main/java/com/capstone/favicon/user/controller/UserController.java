package com.capstone.favicon.user.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RefreshRequest;
import com.capstone.favicon.user.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users/auth")
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto) {
        LoginResponseDto responseDto = userService.login(loginDto);
        return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", responseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<?>> refresh(@RequestBody RefreshRequest request) {
        LoginResponseDto responseDto = userService.refreshToken(request);
        return ResponseEntity.ok().body(APIResponse.successAPI("Token refreshed.", responseDto));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<?>> deleteUser(@AuthenticationPrincipal User user) {
        userService.delete(user);
        return ResponseEntity.ok().body(APIResponse.successAPI("탈퇴하였습니다.", null));
    }

}
