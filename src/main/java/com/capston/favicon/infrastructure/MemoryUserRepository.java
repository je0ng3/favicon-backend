package com.capston.favicon.infrastructure;

import com.capston.favicon.domain.domain.User;
import com.capston.favicon.infrastructure.UserRepository;
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
