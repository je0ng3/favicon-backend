package com.capston.favicon.application;

import com.capston.favicon.application.repository.UserService;
import com.capston.favicon.domain.domain.User;
import com.capston.favicon.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
