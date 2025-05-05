package com.example.movieratingfinder.service;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class PosterValidationService {

    private static final Logger logger = LoggerFactory.getLogger(PosterValidationService.class);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    /**
     * Run every day at 2 AM to validate poster URLs and update the database
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void validatePosterUrls() {
        logger.info("Starting scheduled poster URL validation");

        List<Movie> movies = movieRepository.findAll();
        int total = movies.size();
        int processed = 0;
        int valid = 0;
        int invalid = 0;

        for (Movie movie : movies) {
            processed++;

            if (processed % 100 == 0) {
                logger.info("Processed {}/{} movies", processed, total);
            }

            String posterUrl = movie.getPosterUrl();
            boolean isValidFormat = movieService.isValidPosterUrlFormat(posterUrl);

            if (isValidFormat) {
                boolean exists = movieService.checkPosterUrlExists(posterUrl);
                movie.setPosterValid(exists);

                if (exists) {
                    valid++;
                } else {
                    invalid++;
                }
            } else {
                movie.setPosterValid(false);
                invalid++;
            }

            movieRepository.save(movie);
        }

        logger.info("Poster URL validation completed. Total: {}, Valid: {}, Invalid: {}",
                total, valid, invalid);
    }
}
