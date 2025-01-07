package com.capstone.favicon;

import com.capstone.favicon.application.UserServiceImpl;
import com.capstone.favicon.application.repository.UserService;
import com.capstone.favicon.infrastructure.MemoryUserRepository;

public class AppConfig {

    public UserService userService() {
        return new UserServiceImpl(memoryUserRepository());
    }

    public MemoryUserRepository memoryUserRepository() {
        return new MemoryUserRepository();
    }
}
