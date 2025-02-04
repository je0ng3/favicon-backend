package com.capstone.favicon.application;

import com.capstone.favicon.domain.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public int loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByUsername(username)==null) {
            return 0;
        }
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        return 1;
    }
}



