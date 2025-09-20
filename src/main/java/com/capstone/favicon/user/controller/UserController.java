package com.capstone.favicon.user.controller;

import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/email-check")
    public ResponseEntity<APIResponse<?>> emailCheck(@RequestBody RegisterDto.checkEmail checkEmail) {
        try {
            userService.sendCode(checkEmail);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", checkEmail.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/code-check")
    public ResponseEntity<APIResponse<?>> checkCode(@RequestBody RegisterDto.checkCode checkCode) {
        try {
            userService.checkCode(checkCode);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", checkCode.getCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<?>> register(@RequestBody RegisterDto registerDto) {
        try {
            userService.join(registerDto);
            return ResponseEntity.ok().body(APIResponse.successAPI("Success", registerDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto){
        try {
            LoginResponseDto responseDto = userService.login(loginDto);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", responseDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<?>> deleteUser(@AuthenticationPrincipal User user) {
        try {
            userService.delete(user);
            return ResponseEntity.ok().body(APIResponse.successAPI("탈퇴하였습니다.", null));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

}
