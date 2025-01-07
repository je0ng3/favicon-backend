package com.capstone.favicon.application;

import com.capstone.favicon.application.repository.UserService;
import com.capstone.favicon.domain.domain.User;
import com.capstone.favicon.infrastructure.MemoryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private MemoryUserRepository userRepository;
    public UserServiceImpl(MemoryUserRepository memoryUserRepository) {
        this.userRepository = memoryUserRepository;
    }


    @Override
    public void join(User user) {
        if (userRepository.findByUsername(user.getUsername())!=null) {
            throw new IllegalArgumentException("아이디가 이미 존재합니다. 재확인해주세요.");
        }
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
