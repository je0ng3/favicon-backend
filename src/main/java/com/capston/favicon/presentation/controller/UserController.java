package com.capston.favicon.presentation.controller;

import com.capston.favicon.application.repository.UserService;
import com.capston.favicon.domain.domain.User;
import com.capston.favicon.domain.dto.APIResponse;
import com.capston.favicon.domain.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

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

}
