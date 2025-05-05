package com.example.movieratingfinder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(String toEmail, String username, String verificationCode)
            throws MessagingException, UnsupportedEncodingException {

        String subject = "Please verify your registration";
        String senderName = "Movie Rating Finder";
        String verifyURL = baseUrl + "/verify?code=" + verificationCode;

        String content = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Welcome to Movie Rating Finder!</h2>
                <p>Hi %s,</p>
                <p>Thank you for registering with us. Please verify your email address by clicking the button below:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">Verify Email</a>
                </div>
                <p>Or copy and paste this link in your browser:</p>
                <p><a href="%s">%s</a></p>
                <p>This link will expire in 24 hours.</p>
                <p>If you did not create an account, please ignore this email.</p>
                <p>Best regards,<br>The Movie Rating Finder Team</p>
            </div>
            """.formatted(username, verifyURL, verifyURL, verifyURL);

        sendEmail(toEmail, fromEmail, senderName, subject, content);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String resetToken)
            throws MessagingException, UnsupportedEncodingException {

        String subject = "Password Reset Request";
        String senderName = "Movie Rating Finder";
        String resetURL = baseUrl + "/reset-password?token=" + resetToken;

        String content = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Password Reset Request</h2>
                <p>Hi %s,</p>
                <p>You requested to reset your password. Click the button below to create a new password:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">Reset Password</a>
                </div>
                <p>Or copy and paste this link in your browser:</p>
                <p><a href="%s">%s</a></p>
                <p>This link will expire in 1 hour.</p>
                <p>If you did not request a password reset, please ignore this email.</p>
                <p>Best regards,<br>The Movie Rating Finder Team</p>
            </div>
            """.formatted(username, resetURL, resetURL, resetURL);

        sendEmail(toEmail, fromEmail, senderName, subject, content);
    }

    private void sendEmail(String toEmail, String fromEmail, String senderName, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail, senderName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}
