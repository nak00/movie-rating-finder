package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api/movies")
public class MovieApiController {

    private static final Logger logger = LoggerFactory.getLogger(MovieApiController.class);

    @Autowired
    private MovieService movieService;

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importMovie(@RequestBody Movie movie) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Importing movie from OMDb: {}", movie.getName());

            // Check if movie with same IMDb ID already exists
            if (movie.getImdbId() != null && !movie.getImdbId().isEmpty()) {
                Optional<Movie> existingMovie = movieService.findByImdbId(movie.getImdbId());
                if (existingMovie.isPresent()) {
                    logger.info("Movie already exists with IMDb ID: {}", movie.getImdbId());
                    response.put("success", true);
                    response.put("id", existingMovie.get().getId());
                    response.put("alreadyExists", true);
                    return ResponseEntity.ok(response);
                }
            }

            // If no duplicate found, save the movie
            Movie savedMovie = movieService.saveMovie(movie);
            logger.info("Successfully imported movie with ID: {}", savedMovie.getId());

            response.put("success", true);
            response.put("id", savedMovie.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error importing movie: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
