package com.example.movieratingfinder.controller.user;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public String home(Model model) {
        // Get 10 movies with valid poster URLs
        List<Movie> moviesWithValidPosters = movieService.getMoviesWithValidPosters(10);
        model.addAttribute("topMovies", moviesWithValidPosters);
        return "user/index";
    }

    @GetMapping("/about")
    public String about() {
        return "user/about";
    }
}
