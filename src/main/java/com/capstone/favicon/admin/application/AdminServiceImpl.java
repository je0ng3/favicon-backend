package com.capstone.favicon.admin.application;

import com.capstone.favicon.admin.application.service.AdminService;
import com.capstone.favicon.security.UserSessionRegistry;
import com.capstone.favicon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final UserSessionRegistry userSessionRegistry;

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 세션이 남아 있으면 삭제된 계정으로도 인증이 통과한다
        userRepository.findById(userId)
                .ifPresent(user -> userSessionRegistry.expireAll(user.getEmail()));
        userRepository.deleteByUserId(userId);
    }
}
