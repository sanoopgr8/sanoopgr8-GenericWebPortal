package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Value("${app.base.url}")
    private String appBaseUrl;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EmailService.class);

    public void sendVerificationEmail(String toEmail, String firstName, String verificationToken) {
        logger.info("Attempting to send verification email to: {}", toEmail);
        String verificationLink = appBaseUrl + "/verify?token=" + verificationToken;
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - Web Portal");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Thank you for signing up for Web Portal!\n\n" +
                "Please click the link below to verify your email address:\n" +
                "%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "%s",
                firstName, verificationLink, fromName
            ));
            
            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send verification email to: " + toEmail, e);
            throw e;
        }
    }
}
