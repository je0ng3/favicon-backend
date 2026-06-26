package com.capstone.favicon.admin.application;

import com.capstone.favicon.admin.application.service.AdminService;
import com.capstone.favicon.user.repository.RefreshTokenRepository;
import com.capstone.favicon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        userRepository.deleteByUserId(userId);
    }
}
