package com.capston.favicon.infrastructure;


import com.capston.favicon.domain.domain.User;

public interface UserRepository{
    void save(User user);
    User findByUsername(String username);
}
