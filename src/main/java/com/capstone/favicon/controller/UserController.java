package com.favicon.capstone.controller;

import com.favicon.capstone.user.dto.LoginDto;
import com.favicon.capstone.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userDto", new LoginDto());
        return "login";  // login.html 페이지
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(String username, String password, HttpServletRequest request) {
        boolean isAuthenticated = authService.login(userDto.getId(), userDto.getPassword());
        if (isAuthenticated) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            return "redirect:/user/home";
        } else {
            return "login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/user/login?logout";
    }

    // 홈 페이지
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("id", userId);
        return "home"; // home.html 페이지
    }
}

