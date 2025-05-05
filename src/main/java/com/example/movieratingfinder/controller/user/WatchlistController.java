package com.example.movieratingfinder.controller.user;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import com.example.movieratingfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewWatchlist(Authentication authentication, Model model) {
        model.addAttribute("watchlist", userService.getWatchlist(authentication.getName()));
        return "user/watchlist";
    }

    @GetMapping("/add/{movieId}")
    public String addToWatchlist(@PathVariable Long movieId, Authentication authentication, RedirectAttributes redirectAttributes) {
        userService.addToWatchlist(authentication.getName(), movieId);
        // Add a flag to indicate the movie was added to watchlist
        redirectAttributes.addFlashAttribute("inWatchlist", true);
        // Redirect back to the movie detail page
        return "redirect:/movies/" + movieId;
    }

    @GetMapping("/remove/{movieId}")
    public String removeFromWatchlist(@PathVariable Long movieId, Authentication authentication) {
        userService.removeFromWatchlist(authentication.getName(), movieId);
        return "redirect:/watchlist";
    }
}
