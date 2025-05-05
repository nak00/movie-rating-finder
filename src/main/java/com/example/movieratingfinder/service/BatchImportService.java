package com.example.movieratingfinder.service;

import com.example.movieratingfinder.client.OmdbApiClient;
import com.example.movieratingfinder.dto.OmdbSearchItemDto;
import com.example.movieratingfinder.dto.OmdbSearchResultDto;
import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BatchImportService {

    private static final Logger logger = LoggerFactory.getLogger(BatchImportService.class);
    private static final int RESULTS_PER_PAGE = 10; // OMDb API returns 10 results per page

    @Autowired
    private OmdbApiClient omdbApiClient;

    @Autowired
    private OmdbMovieService omdbMovieService;

    @Autowired
    private MovieRepository movieRepository;

    // Status tracking
    private boolean importInProgress = false;
    private int totalMoviesToProcess = 0;
    private AtomicInteger moviesProcessed = new AtomicInteger(0);
    private AtomicInteger moviesImported = new AtomicInteger(0);
    private AtomicInteger duplicatesSkipped = new AtomicInteger(0);
    private AtomicInteger errors = new AtomicInteger(0);
    private String currentStatus = "Idle";
    private String lastError = "";

    /**
     * Start a batch import process
     * @param searchQuery The search term to use
     * @param pagesToFetch Number of pages to fetch (max 100)
     * @param year Optional year to filter results
     * @return CompletableFuture that completes when the import is done
     */
    @Async
    public CompletableFuture<String> startBatchImport(String searchQuery, int pagesToFetch, Integer year) {
        // Limit pages to 100 (OMDb API limit)
        final int finalPagesToFetch = Math.min(pagesToFetch, 100);

        if (importInProgress) {
            return CompletableFuture.completedFuture("Import already in progress");
        }

        // Reset counters
        importInProgress = true;
        totalMoviesToProcess = 0;
        moviesProcessed.set(0);
        moviesImported.set(0);
        duplicatesSkipped.set(0);
        errors.set(0);

        String yearInfo = (year != null) ? " for year " + year : "";
        currentStatus = "Starting batch import for query: " + searchQuery + yearInfo;
        lastError = "";

        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, get total results count from first page
                OmdbSearchResultDto firstPageResult;
                if (year != null) {
                    firstPageResult = omdbApiClient.searchMoviesByYear(searchQuery, year, 1);
                } else {
                    firstPageResult = omdbApiClient.searchMovies(searchQuery, 1);
                }

                if (firstPageResult == null || !firstPageResult.isSuccess()) {
                    importInProgress = false;
                    currentStatus = "Failed to fetch search results";
                    return "Failed to fetch search results";
                }

                int totalResults = firstPageResult.getTotalResultsAsInt();
                int actualPagesToFetch = Math.min(finalPagesToFetch, (int) Math.ceil(totalResults / (double) RESULTS_PER_PAGE));
                totalMoviesToProcess = Math.min(totalResults, actualPagesToFetch * RESULTS_PER_PAGE);

                currentStatus = String.format("Found %d total results. Processing %d pages (%d movies).",
                        totalResults, actualPagesToFetch, totalMoviesToProcess);
                logger.info(currentStatus);

                // Process first page results
                processSearchResults(firstPageResult.getSearch());

                // Fetch and process remaining pages
                for (int page = 2; page <= actualPagesToFetch; page++) {
                    currentStatus = String.format("Fetching page %d of %d", page, actualPagesToFetch);
                    logger.info(currentStatus);

                    OmdbSearchResultDto pageResult;
                    if (year != null) {
                        pageResult = omdbApiClient.searchMoviesByYear(searchQuery, year, page);
                    } else {
                        pageResult = omdbApiClient.searchMovies(searchQuery, page);
                    }

                    if (pageResult != null && pageResult.isSuccess() && pageResult.getSearch() != null) {
                        processSearchResults(pageResult.getSearch());
                    } else {
                        logger.error("Failed to fetch page {}", page);
                        errors.incrementAndGet();
                    }

                    // Add a small delay to avoid overwhelming the API
                    Thread.sleep(100);
                }

                currentStatus = String.format("Import completed. Processed: %d, Imported: %d, Duplicates: %d, Errors: %d",
                        moviesProcessed.get(), moviesImported.get(), duplicatesSkipped.get(), errors.get());
                logger.info(currentStatus);

                return currentStatus;
            } catch (Exception e) {
                lastError = e.getMessage();
                logger.error("Error during batch import: {}", e.getMessage(), e);
                currentStatus = "Error during import: " + e.getMessage();
                return "Error: " + e.getMessage();
            } finally {
                importInProgress = false;
            }
        });
    }

    /**
     * Overloaded method for backward compatibility
     */
    @Async
    public CompletableFuture<String> startBatchImport(String searchQuery, int pagesToFetch) {
        return startBatchImport(searchQuery, pagesToFetch, null);
    }

    private void processSearchResults(List<OmdbSearchItemDto> searchResults) {
        if (searchResults == null) {
            return;
        }

        for (OmdbSearchItemDto searchItem : searchResults) {
            try {
                // Skip non-movie items
                if (!"movie".equalsIgnoreCase(searchItem.getType())) {
                    continue;
                }

                String imdbId = searchItem.getImdbId();
                if (imdbId == null || imdbId.isEmpty()) {
                    continue;
                }

                // Check if movie already exists in database
                Optional<Movie> existingMovie = movieRepository.findByImdbId(imdbId);
                if (existingMovie.isPresent()) {
                    duplicatesSkipped.incrementAndGet();
                    moviesProcessed.incrementAndGet();
                    continue;
                }

                // Fetch full movie details
                Optional<Movie> movieOpt = omdbMovieService.getMovieById(imdbId);
                if (movieOpt.isPresent()) {
                    Movie movie = movieOpt.get();
                    movieRepository.save(movie);
                    moviesImported.incrementAndGet();
                } else {
                    logger.warn("Could not fetch details for movie with IMDb ID: {}", imdbId);
                    errors.incrementAndGet();
                }

                moviesProcessed.incrementAndGet();

                // Add a small delay to avoid overwhelming the API
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("Error processing movie {}: {}", searchItem.getTitle(), e.getMessage());
                errors.incrementAndGet();
            }
        }
    }

    public BatchImportStatus getStatus() {
        return new BatchImportStatus(
                importInProgress,
                totalMoviesToProcess,
                moviesProcessed.get(),
                moviesImported.get(),
                duplicatesSkipped.get(),
                errors.get(),
                currentStatus,
                lastError
        );
    }

    public static class BatchImportStatus {
        private boolean inProgress;
        private int totalMovies;
        private int processed;
        private int imported;
        private int duplicates;
        private int errors;
        private String status;
        private String lastError;

        public BatchImportStatus(boolean inProgress, int totalMovies, int processed, int imported,
                                 int duplicates, int errors, String status, String lastError) {
            this.inProgress = inProgress;
            this.totalMovies = totalMovies;
            this.processed = processed;
            this.imported = imported;
            this.duplicates = duplicates;
            this.errors = errors;
            this.status = status;
            this.lastError = lastError;
        }

        // Getters
        public boolean isInProgress() { return inProgress; }
        public int getTotalMovies() { return totalMovies; }
        public int getProcessed() { return processed; }
        public int getImported() { return imported; }
        public int getDuplicates() { return duplicates; }
        public int getErrors() { return errors; }
        public String getStatus() { return status; }
        public String getLastError() { return lastError; }
        public int getProgressPercentage() {
            return totalMovies > 0 ? (processed * 100 / totalMovies) : 0;
        }
    }
}
