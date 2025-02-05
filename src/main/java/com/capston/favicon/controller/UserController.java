package com.capston.favicon.controller;

import com.capston.favicon.application.repository.AuthService;
import com.capston.favicon.config.APIResponse;
import com.capston.favicon.domain.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/api/users/login")
    public ResponseEntity<APIResponse<?>> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
        try {
            authService.login(loginDto.getUsername(), loginDto.getPassword(), request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", loginDto.getUsername()));
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
