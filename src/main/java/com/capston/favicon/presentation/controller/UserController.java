package com.capston.favicon.presentation.controller;

import com.capston.favicon.application.repository.AuthService;
import com.capston.favicon.application.repository.UserService;
import com.capston.favicon.domain.domain.User;
import com.capston.favicon.config.APIResponse;
import com.capston.favicon.domain.dto.LoginDto;
import com.capston.favicon.domain.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/api/users/register")
    public ResponseEntity<APIResponse<?>> register(@RequestBody RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        try {
            userService.join(user);
            return ResponseEntity.ok().body(APIResponse.successAPI("회원가입에 성공하였습니다.", registerDto));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }


    @PostMapping("/api/users/login")
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
        try {
            authService.login(loginDto, request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", loginDto.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/api/users/logout")
    public ResponseEntity<APIResponse<?>> logout(HttpServletRequest request){
        try {
            HttpSession session = request.getSession(false);
            String username = session.getAttribute("username").toString();
            authService.logout(request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully logout.", username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }


}
