package com.capstone.favicon.infrastructure;

import com.capstone.favicon.domain.domain.User;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository implements UserRepository {

    private static Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

}
