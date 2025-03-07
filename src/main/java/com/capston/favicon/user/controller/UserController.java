package com.capston.favicon.user.controller;

import com.capston.favicon.user.application.service.UserService;
import com.capston.favicon.config.APIResponse;
import com.capston.favicon.user.dto.LoginDto;
import com.capston.favicon.user.dto.RegisterDto;
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
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully login.", loginDto.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<?>> logout(HttpServletRequest request){
        try {
            HttpSession session = request.getSession(false);
            String username = session.getAttribute("username").toString();
            userService.logout(request);
            return ResponseEntity.ok().body(APIResponse.successAPI("Successfully logout.", username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        }
    }


}
