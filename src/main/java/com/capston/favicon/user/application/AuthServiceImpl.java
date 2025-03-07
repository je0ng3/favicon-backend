package com.capston.favicon.user.application;

import com.capston.favicon.user.application.service.AuthService;
import com.capston.favicon.user.domain.User;
import com.capston.favicon.user.dto.LoginDto;
import com.capston.favicon.user.repository.UserRepository;
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
    public void login(LoginDto loginDto, HttpServletRequest request) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        if (user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("email", email);
        } else {
            throw new BadCredentialsException("Wrong password");
        }

    }

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("email");
    }
}


