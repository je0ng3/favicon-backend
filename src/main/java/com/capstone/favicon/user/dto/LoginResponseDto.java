package com.capstone.favicon.user.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long userId;
    private String username;
    /** 세션 ID. 이후 요청에 Authorization: Bearer 로 실어 보낸다. */
    private String token;

    public LoginResponseDto(Long userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }
}
