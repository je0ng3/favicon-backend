package com.capston.favicon;

import com.capston.favicon.application.UserServiceImpl;
import com.capston.favicon.application.repository.UserService;
import com.capston.favicon.infrastructure.MemoryUserRepository;

public class AppConfig {

    public UserService userService() {
        return new UserServiceImpl(memoryUserRepository());
    }

    public MemoryUserRepository memoryUserRepository() {
        return new MemoryUserRepository();
    }
}
