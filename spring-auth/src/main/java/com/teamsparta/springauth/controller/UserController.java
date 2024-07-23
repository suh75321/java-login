package com.teamsparta.springauth.controller;

import com.teamsparta.springauth.dto.LoginRequestDto;
import com.teamsparta.springauth.dto.SignupRequestDto;
import com.teamsparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")//회원가입
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }


    @PostMapping("/user/signup")
    public String signup(SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return "redirect:/api/user/login-page";
    }

    @PostMapping("/user/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res) {
        try{
            userService.login(requestDto, res);
        }catch (Exception e){
            return "redirect:/api/user/login-page?error";//예외가 발생하면 login-page?error 경로로 리다이렉트하여 사용자에게 오류 메시지
        }
        return"redirect:/";//로그인 성공 시 / 경로로 리다이렉트하여 메인 페이지로 이동
    }

}
