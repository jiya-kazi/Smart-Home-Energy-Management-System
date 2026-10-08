package com.role.implementation.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    @GetMapping("/admin")
    public String adminHome(Model model) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            model.addAttribute("userDetails", auth.getName());
        }

        return "admin-home";
    }
}
