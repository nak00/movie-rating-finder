package com.example.movieratingfinder.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    @GetMapping
    public String settings(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Add any settings data here
        return "admin/settings";
    }

    @GetMapping("/logs")
    public String activityLogs(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Add log data here
        return "admin/activity-logs";
    }
}
