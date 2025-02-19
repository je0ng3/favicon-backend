package com.capston.favicon.application.repository;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void login(String username, String password, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
