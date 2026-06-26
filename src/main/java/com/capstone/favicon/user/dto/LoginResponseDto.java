package com.capstone.favicon.user.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long userId;
    private String username;
    private String token;
    private String refresh;

    public LoginResponseDto(Long userId, String username, String token, String refresh) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.refresh = refresh;
    }
}
