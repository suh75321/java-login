package com.teamsparta.springauth.controller;

import com.teamsparta.springauth.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {// 홈페이지 관리

    @GetMapping("/")//AuthenticationPrincipal 추가해 UserDetailsImpl와 연결
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 페이지 동적 처리 : 사용자 이름
        model.addAttribute("username", userDetails.getUser().getUsername());

        return "index";
    }
}
