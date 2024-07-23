package com.teamsparta.springauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {// 홈페이지 관리

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("username", "username");
        return "index";
    }

}
