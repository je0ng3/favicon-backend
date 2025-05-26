package com.capstone.favicon.admin.application;

import com.capstone.favicon.admin.application.service.AdminService;
import com.capstone.favicon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteByUserId(userId);
    }
}
