package com.capstone.favicon.user.controller;

import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.config.APIResponse;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
        try {
            userService.login(loginDto, request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", loginDto.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<?>> logout(HttpServletRequest request){
        try {
            HttpSession session = request.getSession(false);
            String email = session.getAttribute("email").toString();
            userService.logout(request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully logout.", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<?>> deleteUser(HttpServletRequest request) {
        try {
            userService.delete(request);
            return ResponseEntity.ok().body(APIResponse.successAPI("탈퇴하였습니다.", null));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

    @GetMapping("/session-check")
    public ResponseEntity<APIResponse<?>> checkSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String email = session.getAttribute("email").toString();
            return ResponseEntity.ok().body(APIResponse.successAPI("로그인 상태.", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI("로그인 상태 아님."));
        }

    }

    @GetMapping("/admin-check")
    public ResponseEntity<APIResponse<?>> checkAdmin(HttpServletRequest request) {
        try {
            boolean isAdmin = userService.checkAdmin(request);
            if (isAdmin) {
                return ResponseEntity.ok().body(APIResponse.successAPI("관리자", null));
            } else {
                return ResponseEntity.ok().body(APIResponse.successAPI("일반 사용자", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

}
