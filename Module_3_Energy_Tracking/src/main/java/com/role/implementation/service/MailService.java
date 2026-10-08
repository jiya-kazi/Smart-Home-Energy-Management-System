//package com.role.implementation.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendResetLink(String toEmail, String resetLink) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("shaik6553556@gmail.com");  // ✅ ADD THIS LINE
//        message.setTo(toEmail);
//        message.setSubject("SHEMS - Reset Password Link");
//        message.setText("Click the link to reset your password:\n" + resetLink);
//
//        mailSender.send(message);
//    }
//}

package com.role.implementation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetLink(String toEmail, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);   // ✅ automatic sender
        message.setTo(toEmail);
        message.setSubject("SHEMS - Reset Password Link");
        message.setText("Click the link to reset your password:\n" + resetLink);

        mailSender.send(message);
    }
}
