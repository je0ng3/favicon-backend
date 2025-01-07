package com.capstone.favicon.infrastructure;


import com.capstone.favicon.domain.domain.User;

public interface UserRepository{
    void save(User user);
    User findByUsername(String username);
}
