package com.example.movieratingfinder.service;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private OmdbMovieService omdbMovieService;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Page<Movie> getAllMoviesPaged(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Page<Movie> getAllMoviesWithValidPostersFirst(int page, int size, String sortField) {
        logger.debug("Getting all movies with valid posters first, page: {}, size: {}, sort: {}", page, size, sortField);

        // Get all movies with valid posters sorted by the requested field
        Sort sort = Sort.by(sortField);
        List<Movie> validMovies = movieRepository.findByPosterValidTrue(sort);
        logger.debug("Found {} movies with valid posters", validMovies.size());

        // Get all movies with invalid posters sorted by the requested field
        List<Movie> invalidMovies = movieRepository.findByPosterValidFalseOrPosterValidIsNull(sort);
        logger.debug("Found {} movies with invalid posters", invalidMovies.size());

        // Combine the lists: valid posters first, then invalid posters
        List<Movie> allMovies = new ArrayList<>();
        allMovies.addAll(validMovies);
        allMovies.addAll(invalidMovies);

        // Calculate total elements
        int totalElements = allMovies.size();
        logger.debug("Total movies: {}", totalElements);

        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        // Handle out of bounds page request
        if (start >= totalElements) {
            logger.debug("Page start index {} is out of bounds for total elements {}", start, totalElements);
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size, sort), totalElements);
        }

        logger.debug("Returning page content from index {} to {}", start, end);
        List<Movie> pageContent = allMovies.subList(start, end);

        // Create a Page object with our manually paginated content
        return new PageImpl<>(pageContent, PageRequest.of(page, size, sort), totalElements);
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Optional<Movie> getMovieByImdbId(String imdbId) {
        // First check if we have it in our database
        Optional<Movie> localMovie = movieRepository.findByImdbId(imdbId);
        if (localMovie.isPresent()) {
            return localMovie;
        }

        // If not, fetch from OMDb API and save to our database
        Optional<Movie> omdbMovie = omdbMovieService.getMovieById(imdbId);
        if (omdbMovie.isPresent()) {
            Movie movie = omdbMovie.get();
            return Optional.of(movieRepository.save(movie));
        }

        return Optional.empty();
    }

    // Search only in local database
    public List<Movie> searchLocalMovies(String query) {
        return movieRepository.findByNameContainingIgnoreCase(query);
    }

    // Search only in local database with pagination
    public Page<Movie> searchLocalMoviesPaged(String query, Pageable pageable) {
        return movieRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    // Search with valid posters first, then invalid posters
    public Page<Movie> searchLocalMoviesWithValidPostersFirst(String query, int page, int size, String sortField) {
        logger.debug("Searching movies with valid posters first, query: {}, page: {}, size: {}, sort: {}",
                query, page, size, sortField);

        Sort sort = Sort.by(sortField);

        // Get all matching movies with valid posters
        List<Movie> validMovies = movieRepository.findByNameContainingIgnoreCaseAndPosterValidTrue(query, sort);
        logger.debug("Found {} matching movies with valid posters", validMovies.size());

        // Get all matching movies with invalid posters
        List<Movie> invalidMovies = movieRepository.findByNameContainingIgnoreCaseAndPosterValidFalseOrPosterValidIsNull(query, sort);
        logger.debug("Found {} matching movies with invalid posters", invalidMovies.size());

        // Combine the lists
        List<Movie> allMovies = new ArrayList<>();
        allMovies.addAll(validMovies);
        allMovies.addAll(invalidMovies);

        // Calculate total elements
        int totalElements = allMovies.size();
        logger.debug("Total matching movies: {}", totalElements);

        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        // Handle out of bounds page request
        if (start >= totalElements) {
            logger.debug("Page start index {} is out of bounds for total elements {}", start, totalElements);
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size, sort), totalElements);
        }

        logger.debug("Returning page content from index {} to {}", start, end);
        List<Movie> pageContent = allMovies.subList(start, end);

        // Create a Page object with our manually paginated content
        return new PageImpl<>(pageContent, PageRequest.of(page, size, sort), totalElements);
    }

    // Original search method - kept for backward compatibility
    public List<Movie> searchMovies(String query) {
        return searchLocalMovies(query);
    }

    // Original paged search method - kept for backward compatibility
    public Page<Movie> searchMoviesPaged(String query, Pageable pageable) {
        return searchLocalMoviesPaged(query, pageable);
    }

    public List<Movie> getTopRatedMovies() {
        return movieRepository.findTop10ByOrderByImdbRatingDesc();
    }

    /**
     * Get movies with valid poster URLs until reaching the desired count
     * Using the posterValid flag to avoid HTTP checks
     * @param count Number of movies with valid posters to collect
     * @return List of movies with valid poster URLs
     */
    public List<Movie> getMoviesWithValidPosters(int count) {
        int page = 0;
        int pageSize = 20;
        List<Movie> result = new ArrayList<>();

        while (result.size() < count) {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by("imdbRating").descending());

            // Use the posterValid flag to filter movies
            Page<Movie> moviePage = movieRepository.findByPosterValidTrue(pageable);

            if (moviePage.isEmpty()) {
                logger.info("No more movies with valid posters available. Returning {} found so far.", result.size());

                // If we don't have enough movies with validated posters,
                // fall back to the HTTP validation method for the remaining ones
                if (result.size() < count) {
                    logger.info("Falling back to HTTP validation to find more valid posters");
                    List<Movie> additionalMovies = findMoreMoviesWithValidPosters(count - result.size(), page * pageSize);
                    result.addAll(additionalMovies);
                }
                break;
            }

            for (Movie movie : moviePage.getContent()) {
                result.add(movie);
                if (result.size() >= count) {
                    break;
                }
            }

            page++;
            if (page > 50) {
                logger.warn("Reached maximum page limit while collecting movies with valid posters.");
                break;
            }
        }

        logger.info("Returning {} movies with valid posters", result.size());
        return result;
    }

    /**
     * Fallback method to find more movies with valid posters using HTTP validation
     */
    private List<Movie> findMoreMoviesWithValidPosters(int count, int skip) {
        List<Movie> result = new ArrayList<>();
        int page = skip / 20; // Calculate starting page based on skip count
        int pageSize = 20;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        while (result.size() < count) {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by("imdbRating").descending());
            Page<Movie> moviePage = movieRepository.findByPosterValidFalseOrPosterValidIsNull(pageable);

            if (moviePage.isEmpty()) {
                break;
            }

            for (Movie movie : moviePage.getContent()) {
                String url = movie.getPosterUrl();

                if (!isValidPosterUrlFormat(url)) {
                    continue;
                }

                boolean isValid = checkPosterUrlExists(url);
                // Update the posterValid flag in the database
                movie.setPosterValid(isValid);
                movieRepository.save(movie);

                if (isValid) {
                    result.add(movie);
                    if (result.size() >= count) {
                        break;
                    }
                }
            }

            page++;
            if (page > 50) {
                break;
            }
        }

        return result;
    }

    /**
     * Check if a poster URL format is valid before HTTP validation
     * @param url The poster URL to check
     * @return true if the URL format is valid, false otherwise
     */
    public boolean isValidPosterUrlFormat(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        String lowerUrl = url.toLowerCase();
        if (lowerUrl.equals("n/a") ||
                lowerUrl.equals("na") ||
                lowerUrl.contains("no_image") ||
                lowerUrl.contains("placeholder") ||
                !lowerUrl.startsWith("http")) {
            return false;
        }

        return true;
    }

    /**
     * Check if a poster URL exists by making an HTTP HEAD request
     * Results are cached to avoid repeated checks
     * @param url The poster URL to check
     * @return true if the URL exists and returns a 200 status code
     */
    @Cacheable(value = "posterUrlValidation", key = "#url")
    public boolean checkPosterUrlExists(String url) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(2))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            boolean valid = response.statusCode() == 200;
            logger.debug("Poster URL {} validation result: {}", url, valid);
            return valid;
        } catch (Exception e) {
            logger.debug("Failed to verify image URL: {}, Error: {}", url, e.getMessage());
            return false;
        }
    }

    public Page<Movie> findMoviesByYear(int year, Pageable pageable) {
        return movieRepository.findByYear(year, pageable);
    }

    public Page<Movie> findMoviesByYearWithValidPostersFirst(int year, int page, int size, String sortField) {
        logger.debug("Finding movies by year with valid posters first, year: {}, page: {}, size: {}, sort: {}",
                year, page, size, sortField);

        Sort sort = Sort.by(sortField);

        // Get all movies from the specified year with valid posters
        List<Movie> validMovies = movieRepository.findByYearAndPosterValidTrue(year, sort);
        logger.debug("Found {} movies from year {} with valid posters", validMovies.size(), year);

        // Get all movies from the specified year with invalid posters
        List<Movie> invalidMovies = movieRepository.findByYearAndPosterValidFalseOrPosterValidIsNull(year, sort);
        logger.debug("Found {} movies from year {} with invalid posters", invalidMovies.size(), year);

        // Combine the lists
        List<Movie> allMovies = new ArrayList<>();
        allMovies.addAll(validMovies);
        allMovies.addAll(invalidMovies);

        // Calculate total elements
        int totalElements = allMovies.size();
        logger.debug("Total movies from year {}: {}", year, totalElements);

        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        // Handle out of bounds page request
        if (start >= totalElements) {
            logger.debug("Page start index {} is out of bounds for total elements {}", start, totalElements);
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size, sort), totalElements);
        }

        logger.debug("Returning page content from index {} to {}", start, end);
        List<Movie> pageContent = allMovies.subList(start, end);

        // Create a Page object with our manually paginated content
        return new PageImpl<>(pageContent, PageRequest.of(page, size, sort), totalElements);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public long getMovieCount() {
        return movieRepository.count();
    }

    public Optional<Movie> findByImdbId(String imdbId) {
        return movieRepository.findByImdbId(imdbId);
    }

    public List<Movie> findMoviesWithInvalidPosters() {
        return movieRepository.findMoviesWithInvalidPosters();
    }
}
