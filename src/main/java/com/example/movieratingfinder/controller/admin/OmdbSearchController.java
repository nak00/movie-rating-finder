package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import com.example.movieratingfinder.service.OmdbMovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/admin/omdb")
public class OmdbSearchController {

    private static final Logger logger = LoggerFactory.getLogger(OmdbSearchController.class);

    @Autowired
    private OmdbMovieService omdbMovieService;

    @Autowired
    private MovieService movieService;

    @GetMapping("/search")
    public String searchForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin/omdb-search";
    }

    @PostMapping("/search")
    public String search(@RequestParam String title, Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());

        try {
            logger.info("Searching OMDb for title: {}", title);
            Optional<Movie> movie = omdbMovieService.getMovieByTitle(title);
            if (movie.isPresent()) {
                model.addAttribute("movie", movie.get());
                model.addAttribute("searchSuccess", true);
                logger.info("Found movie: {}", movie.get().getName());
            } else {
                model.addAttribute("searchError", "Movie not found");
                logger.info("Movie not found: {}", title);
            }
        } catch (Exception e) {
            model.addAttribute("searchError", "Error searching for movie: " + e.getMessage());
            logger.error("Error searching for movie: {}", e.getMessage(), e);
        }

        return "admin/omdb-search";
    }

    @PostMapping("/import")
    public String importMovie(@ModelAttribute Movie movie) {
        try {
            logger.info("Importing movie from OMDb: {}", movie.getName());
            Movie savedMovie = movieService.saveMovie(movie);
            logger.info("Successfully imported movie with ID: {}", savedMovie.getId());
            return "redirect:/admin/movies";
        } catch (Exception e) {
            logger.error("Error importing movie: {}", e.getMessage(), e);
            return "redirect:/admin/omdb/search?error=import";
        }
    }
}
