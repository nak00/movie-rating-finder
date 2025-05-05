package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.dto.MovieDto;
import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {

    private static final Logger logger = LoggerFactory.getLogger(AdminMovieController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private Environment environment;

    @GetMapping
    public String listMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            Model model,
            HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());

        Pageable pageable;
        if (sortBy.equals("imdbRating")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy));
        }

        Page<Movie> moviePage;

        if (search != null && !search.isEmpty()) {
            // Only search in local database
            moviePage = movieService.searchLocalMoviesPaged(search, pageable);
            model.addAttribute("search", search);
        } else if (year != null) {
            moviePage = movieService.findMoviesByYear(year, pageable);
            model.addAttribute("year", year);
        } else {
            moviePage = movieService.getAllMoviesPaged(pageable);
        }

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("totalItems", moviePage.getTotalElements());
        model.addAttribute("sortBy", sortBy);

        return "admin/movies";
    }

    @GetMapping("/new")
    public String newMovieForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("movie", new Movie());
        return "admin/movie-form";
    }

    @GetMapping("/search-omdb")
    public String searchOmdbForm(Model model) {
        // Add the API key to the model
        model.addAttribute("apiKey", environment.getProperty("omdb.api.key"));
        return "admin/omdb-search";
    }

    @PostMapping
    public String saveMovie(@ModelAttribute Movie movie) {
        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        Optional<Movie> movieOpt = movieService.getMovieById(id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
            return "admin/movie-form";
        } else {
            return "redirect:/admin/movies?error=movie-not-found";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }

    @GetMapping("/invalid-posters")
    @ResponseBody
    public List<MovieDto> findInvalidPosters() {
        List<Movie> moviesWithInvalidPosters = movieService.findMoviesWithInvalidPosters();

        // Convert to DTOs with only the fields we need
        return moviesWithInvalidPosters.stream()
                .map(movie -> new MovieDto(
                        movie.getId(),
                        movie.getName(),
                        movie.getPosterUrl()
                ))
                .collect(Collectors.toList());
    }
}
