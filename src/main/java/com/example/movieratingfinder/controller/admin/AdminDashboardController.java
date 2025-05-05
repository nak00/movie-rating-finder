package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.service.MovieService;
import com.example.movieratingfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // Add the current URI to the model
        model.addAttribute("currentUri", request.getRequestURI());

        // Add dashboard statistics
        model.addAttribute("movieCount", movieService.getAllMovies().size());

        // If you have user count method
        // model.addAttribute("userCount", userService.getUserCount());

        // For now, add placeholder values for stats that might not be implemented yet
        model.addAttribute("userCount", 0);
        model.addAttribute("watchlistCount", 0);
        model.addAttribute("reviewCount", 0);

        return "admin/dashboard";
    }
}
