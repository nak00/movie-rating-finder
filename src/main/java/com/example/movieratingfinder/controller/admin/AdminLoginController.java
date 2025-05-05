package com.example.movieratingfinder.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {

    @GetMapping("/admin-login")
    public String adminLoginPage() {
        return "admin/admin-login";
    }
}
