package com.capstone.favicon.admin.controller;

import com.capstone.favicon.admin.application.service.AdminService;
import com.capstone.favicon.config.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @DeleteMapping("/delete-user")
    public ResponseEntity<APIResponse<?>> deleteUser(@RequestParam("userId") Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok().body(APIResponse.successAPI("탈퇴시켰습니다.", userId));
        } catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(message));
        }
    }
}
