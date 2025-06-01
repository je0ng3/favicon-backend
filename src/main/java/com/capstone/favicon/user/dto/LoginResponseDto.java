package com.capstone.favicon.user.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long userId;
    private String username;

    public LoginResponseDto(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
