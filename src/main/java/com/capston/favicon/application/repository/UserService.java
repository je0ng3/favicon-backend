package com.capston.favicon.application.repository;

import com.capston.favicon.domain.domain.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void join(User user);
    void delete(HttpServletRequest request);
}
