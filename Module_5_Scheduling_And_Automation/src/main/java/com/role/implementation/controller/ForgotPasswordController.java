//package com.role.implementation.controller;
//
//import com.role.implementation.model.User;
//import com.role.implementation.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//public class ForgotPasswordController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @GetMapping("/forgot-password")
//    public String forgotPasswordPage() {
//        return "forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String checkEmail(@RequestParam("email") String email, Model model) {
//
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            model.addAttribute("error", "Email not found ❌");
//            return "forgot-password";
//        }
//
//        model.addAttribute("email", email);
//        return "reset-password";
//    }
//
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestParam("email") String email,
//                                @RequestParam("newPassword") String newPassword,
//                                Model model) {
//
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            model.addAttribute("error", "Invalid email ❌");
//            return "forgot-password";
//        }
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        model.addAttribute("msg", "Password updated successfully ✅");
//        model.addAttribute("email", email);
//        return "reset-password";
//    }
//}


package com.role.implementation.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.role.implementation.model.PasswordResetToken;
import com.role.implementation.model.User;
import com.role.implementation.repository.PasswordResetTokenRepository;
import com.role.implementation.repository.UserRepository;
import com.role.implementation.service.MailService;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 1) Open forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // 2) Generate token + send mail
    @Transactional
    @PostMapping("/forgot-password")
    public String sendResetLink(@RequestParam("email") String email, Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Email not registered ❌");
            return "forgot-password";
        }

        // delete old tokens
        passwordResetTokenRepository.deleteByEmail(email);

        // create token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        // mail link
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        // send mail
        mailService.sendResetLink(email, resetLink);

        model.addAttribute("msg", "✅ Reset link sent to your email. Please check Inbox/Spam.");
        return "message";
    }

    // 3) When user clicks link in mail
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElse(null);

        if (resetToken == null || resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("msg", "Invalid or expired reset link ❌");
            return "message";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // 4) Update new password
    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam("token") String token,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElse(null);

        if (resetToken == null || resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("msg", "Invalid or expired reset link ❌");
            return "message";
        }

        User user = userRepository.findByEmail(resetToken.getEmail());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        model.addAttribute("msg", "✅ Password updated successfully. Now login.");
        return "message";
    }
}
