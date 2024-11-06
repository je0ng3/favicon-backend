package com.capston.favicon.application.repository;

import com.capston.favicon.domain.domain.User;

public interface UserService {
    void join(User user);
    User findByUsername(String username);
}
