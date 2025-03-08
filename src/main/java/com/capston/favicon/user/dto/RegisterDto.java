package com.capston.favicon.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {

    private String email;
    private String username;
    private String password;

    @Getter
    public static class checkEmail {
        private String email;
    }

    @Getter
    public static class checkCode {
        private String email;
        private String code;
    }

}