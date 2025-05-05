package com.example.movieratingfinder.service;

import com.example.movieratingfinder.client.OmdbApiClient;
import com.example.movieratingfinder.dto.OmdbMovieDto;
import com.example.movieratingfinder.dto.OmdbSearchItemDto;
import com.example.movieratingfinder.dto.OmdbSearchResultDto;
import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OmdbMovieService {

    private static final Logger logger = LoggerFactory.getLogger(OmdbMovieService.class);

    @Autowired
    private OmdbApiClient omdbApiClient;

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> searchMovies(String query) {
        try {
            OmdbSearchResultDto result = omdbApiClient.searchMovies(query, 1);
            if (result != null && result.isSuccess() && result.getSearch() != null) {
                return result.getSearch().stream()
                        .map(this::convertToMovie)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error searching movies: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public Page<Movie> searchMoviesPaged(String query, Pageable pageable) {
        try {
            // Calculate OMDb page (1-based) from Spring pageable (0-based)
            int omdbPage = pageable.getPageNumber() + 1;

            OmdbSearchResultDto result = omdbApiClient.searchMovies(query, omdbPage);
            if (result != null && result.isSuccess() && result.getSearch() != null) {
                List<Movie> movies = result.getSearch().stream()
                        .map(this::convertToMovie)
                        .collect(Collectors.toList());

                int totalResults = result.getTotalResultsAsInt();
                return new PageImpl<>(movies, pageable, totalResults);
            }
        } catch (Exception e) {
            logger.error("Error searching movies paged: {}", e.getMessage());
        }

        return Page.empty(pageable);
    }

    public Optional<Movie> getMovieById(String imdbId) {
        try {
            OmdbMovieDto omdbMovie = omdbApiClient.getMovieById(imdbId);
            if (omdbMovie != null && omdbMovie.isSuccess()) {
                return Optional.of(convertToMovie(omdbMovie));
            }
        } catch (Exception e) {
            logger.error("Error getting movie by ID: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Movie> getMovieByTitle(String title) {
        try {
            OmdbMovieDto omdbMovie = omdbApiClient.getMovieByTitle(title);
            if (omdbMovie != null && omdbMovie.isSuccess()) {
                return Optional.of(convertToMovie(omdbMovie));
            }
        } catch (Exception e) {
            logger.error("Error getting movie by title: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    private Movie convertToMovie(OmdbSearchItemDto searchItem) {
        Movie movie = new Movie();
        movie.setName(searchItem.getTitle());
        movie.setPosterUrl(searchItem.getPoster());

        // Extract year from string like "1994" or "1994-1996"
        try {
            String yearStr = searchItem.getYear();
            if (yearStr != null) {
                if (yearStr.contains("–")) {
                    yearStr = yearStr.split("–")[0];
                }
                movie.setYear(Integer.parseInt(yearStr));
            } else {
                movie.setYear(0);
            }
        } catch (NumberFormatException e) {
            movie.setYear(0);
        }

        // Set temporary values for fields not available in search results
        movie.setDescription("Click for details");
        movie.setImdbRating(0.0);
        movie.setRottenTomatoesRating(0);

        // Store IMDb ID in the database for later reference
        movie.setImdbId(searchItem.getImdbId());

        return movie;
    }

    private Movie convertToMovie(OmdbMovieDto omdbMovie) {
        Movie movie = new Movie();
        movie.setName(omdbMovie.getTitle());
        movie.setPosterUrl(omdbMovie.getPoster());
        movie.setDescription(omdbMovie.getPlot());
        movie.setImdbId(omdbMovie.getImdbId());

        // Extract year
        try {
            String yearStr = omdbMovie.getYear();
            if (yearStr != null && !yearStr.equals("N/A")) {
                if (yearStr.contains("–")) {
                    yearStr = yearStr.split("–")[0];
                }
                movie.setYear(Integer.parseInt(yearStr));
            } else {
                movie.setYear(0);
            }
        } catch (NumberFormatException e) {
            movie.setYear(0);
        }

        // Extract IMDb rating
        try {
            if (omdbMovie.getImdbRating() != null && !omdbMovie.getImdbRating().equals("N/A")) {
                movie.setImdbRating(Double.parseDouble(omdbMovie.getImdbRating()));
            } else {
                movie.setImdbRating(0.0);
            }
        } catch (NumberFormatException e) {
            movie.setImdbRating(0.0);
        }

        // Calculate Rotten Tomatoes rating from Metascore
        try {
            if (omdbMovie.getMetascore() != null && !omdbMovie.getMetascore().equals("N/A")) {
                movie.setRottenTomatoesRating(Integer.parseInt(omdbMovie.getMetascore()));
            } else {
                // Estimate RT rating from IMDb rating
                movie.setRottenTomatoesRating((int) Math.round(movie.getImdbRating() * 10));
            }
        } catch (NumberFormatException e) {
            movie.setRottenTomatoesRating(0);
        }

        return movie;
    }
}
