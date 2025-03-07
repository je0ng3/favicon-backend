package com.capston.favicon.user.application.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void login(String username, String password, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
