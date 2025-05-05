package com.example.movieratingfinder.controller.user;

import com.example.movieratingfinder.model.User;
import com.example.movieratingfinder.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes) {
        // Validate password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "Passwords do not match");
            return "redirect:/signup";
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/signup";
        } catch (MessagingException | UnsupportedEncodingException e) {
            redirectAttributes.addAttribute("error", "Failed to send verification email");
            return "redirect:/signup";
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam String code, RedirectAttributes redirectAttributes) {
        boolean verified = userService.verifyUser(code);

        if (verified) {
            redirectAttributes.addAttribute("verified", "true");
        } else {
            redirectAttributes.addAttribute("error", "Invalid or expired verification code");
        }

        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.initiatePasswordReset(email);
            redirectAttributes.addAttribute("message", "If your email exists in our system, you will receive a password reset link");
            return "redirect:/login";
        } catch (MessagingException | UnsupportedEncodingException e) {
            redirectAttributes.addAttribute("error", "Failed to send password reset email");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addAttribute("token", token);
            redirectAttributes.addAttribute("error", "Passwords do not match");
            return "redirect:/reset-password";
        }

        boolean success = userService.resetPassword(token, password);

        if (success) {
            redirectAttributes.addAttribute("reset", "true");
            return "redirect:/login";
        } else {
            redirectAttributes.addAttribute("error", "Invalid or expired token");
            return "redirect:/forgot-password";
        }
    }
}
