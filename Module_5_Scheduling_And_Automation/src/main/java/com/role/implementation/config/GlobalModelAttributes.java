package com.role.implementation.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userName", authentication.getName());
        }
    }
}
