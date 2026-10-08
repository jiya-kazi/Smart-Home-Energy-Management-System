package com.role.implementation.controller;

import com.role.implementation.devicemanagement.service.DeviceService;
import com.role.implementation.model.User;
import com.role.implementation.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserRepository userRepository;

    // 🌐 HOME PAGE
    @GetMapping("/")
    public String publicHome(Model model, Authentication authentication) {

        // ✅ Execute ONLY for real logged-in users
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            long totalDevices = deviceService.countTotalDevices();
            long activeDevices = deviceService.countActiveDevices();
            long inactiveDevices = totalDevices - activeDevices;

            // ✅ Username will NEVER be null
            if (user != null) {
                model.addAttribute("userName", user.getName());
            } else {
                model.addAttribute("userName", email); // safe fallback
            }

            model.addAttribute("totalDevices", totalDevices);
            model.addAttribute("activeDevices", activeDevices);
            model.addAttribute("inactiveDevices", inactiveDevices);
        }

        return "home"; // home.html
    }
}
