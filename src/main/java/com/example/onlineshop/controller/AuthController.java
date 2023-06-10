package com.example.onlineshop.controller;
import com.example.onlineshop.entity.dto.AuthDto;
import com.example.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/auth")
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final HomePageController homePageController;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute AuthDto authDto, Model model,Principal principal) {
            userService.validUser(authDto.getUsername(),authDto.getPassword(),"login");
            return homePageController.loginPage();
    }

}
