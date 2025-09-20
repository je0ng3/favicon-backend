package com.capstone.favicon.user.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long userId;
    private String username;
    private String token;

    public LoginResponseDto(Long userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }
}
