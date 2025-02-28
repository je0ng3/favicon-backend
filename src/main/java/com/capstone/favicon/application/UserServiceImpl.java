package com.capstone.favicon.application;

import com.capstone.favicon.application.repository.UserService;
import com.capstone.favicon.domain.domain.User;
import com.capstone.favicon.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void join(User user) {
        String name = user.getUsername();
        if (userRepository.findByUsername(name)!=null) {
            throw new IllegalArgumentException("아이디가 이미 존재합니다. 재확인해주세요.");
        }
        userRepository.save(user);
    }

}
