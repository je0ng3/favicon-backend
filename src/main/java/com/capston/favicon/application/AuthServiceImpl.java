package com.capston.favicon.application;

import com.capston.favicon.application.repository.AuthService;
import com.capston.favicon.domain.domain.User;
import com.capston.favicon.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public void login(String username, String password, HttpServletRequest request) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
        } else {
            throw new BadCredentialsException("Wrong password");
        }

    }

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("username");
    }
}


