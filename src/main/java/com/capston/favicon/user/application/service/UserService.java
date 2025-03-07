package com.capston.favicon.user.application.service;

import com.capston.favicon.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void join(User user);
    void delete(HttpServletRequest request);
}
