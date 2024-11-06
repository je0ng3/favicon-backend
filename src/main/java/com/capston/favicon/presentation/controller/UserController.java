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
        User user = userService.findByUsername(registerDto.getUsername());
        try {
            userService.join(user);
            return ResponseEntity.ok().body(APIResponse.successAPI("회원가입에 성공하였습니다.", registerDto));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }

    @GetMapping("/api/users/check-username/{username}")
    public ResponseEntity<APIResponse<?>> checkUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.ok().body(APIResponse.successAPI("사용가능한 이름입니다.", username));
        }
        else {
            return ResponseEntity.badRequest().body(APIResponse.successAPI("이미 존재하는 이름입니다. 다른 이름을 선택해주세요.", username));
        }
    }
}
