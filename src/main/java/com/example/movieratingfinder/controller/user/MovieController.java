package com.example.movieratingfinder.controller.user;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import com.example.movieratingfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sort,
            Model model) {

        // Handle empty search parameter differently based on context
        // For direct search submissions, show 404 if empty
        // For pagination with empty search parameter, just show all movies
        boolean isEmptySearch = search != null && search.trim().isEmpty();
        boolean isDirectSearchSubmission = isEmptySearch && page == 0; // Assuming direct searches start at page 0

        if (isDirectSearchSubmission) {
            // Only throw 404 for direct empty search submissions
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No search term provided");
        }

        Page<Movie> moviePage;

        if (search != null && !search.trim().isEmpty()) {
            // Search with valid posters first
            moviePage = movieService.searchLocalMoviesWithValidPostersFirst(search, page, size, sort);
            model.addAttribute("searchTerm", search);
        } else if (year != null) {
            // Filter by year with valid posters first
            moviePage = movieService.findMoviesByYearWithValidPostersFirst(year, page, size, sort);
            model.addAttribute("year", year);
        } else {
            // All movies with valid posters first
            moviePage = movieService.getAllMoviesWithValidPostersFirst(page, size, sort);
        }

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("totalItems", moviePage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "user/movies";
    }

    @GetMapping("/{id}")
    public String movieDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());

            // Check if user is logged in and if movie is in their watchlist
            if (authentication != null) {
                boolean inWatchlist = userService.isInWatchlist(authentication.getName(), id);
                if (inWatchlist) {
                    model.addAttribute("inWatchlist", true);
                }
            }

            return "user/movie-detail";
        } else {
            return "redirect:/movies";
        }
    }

    @GetMapping("/imdb/{imdbId}")
    public String movieDetailByImdbId(@PathVariable String imdbId, Model model, Authentication authentication) {
        Optional<Movie> movie = movieService.getMovieByImdbId(imdbId);
        if (movie.isPresent()) {
            Movie savedMovie = movie.get();

            // Check if user is logged in and if movie is in their watchlist
            if (authentication != null && savedMovie.getId() != null) {
                boolean inWatchlist = userService.isInWatchlist(authentication.getName(), savedMovie.getId());
                if (inWatchlist) {
                    model.addAttribute("inWatchlist", true);
                }
            }

            model.addAttribute("movie", savedMovie);
            return "user/movie-detail";
        } else {
            return "redirect:/movies";
        }
    }
}
