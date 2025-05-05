package com.example.movieratingfinder.client;

import com.example.movieratingfinder.dto.OmdbMovieDto;
import com.example.movieratingfinder.dto.OmdbSearchResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OmdbApiClient {

    private static final Logger logger = LoggerFactory.getLogger(OmdbApiClient.class);

    private final HttpClient httpClient;
    private final String apiUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public OmdbApiClient(
            @Value("${omdb.api.url}") String apiUrl,
            @Value("${omdb.api.key}") String apiKey) {

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();

        // Remove trailing slash if present
        this.apiUrl = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
        this.apiKey = apiKey;

        logger.info("OmdbApiClient initialized with URL: {}", this.apiUrl);
    }

    public OmdbMovieDto getMovieById(String imdbId) {
        String url = apiUrl + "?apikey=" + apiKey + "&i=" + encodeValue(imdbId);
        logger.debug("Fetching movie by ID: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.debug("Response body: {}", response.body());
                return objectMapper.readValue(response.body(), OmdbMovieDto.class);
            } else {
                logger.error("Error response: {} {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error fetching movie by ID {}: {}", imdbId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch movie by ID: " + e.getMessage(), e);
        }
    }

    public OmdbMovieDto getMovieByTitle(String title) {
        String url = apiUrl + "?apikey=" + apiKey + "&t=" + encodeValue(title);
        logger.debug("Fetching movie by title: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.debug("Response body: {}", response.body());
                return objectMapper.readValue(response.body(), OmdbMovieDto.class);
            } else {
                logger.error("Error response: {} {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error fetching movie by title {}: {}", title, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch movie by title: " + e.getMessage(), e);
        }
    }

    public OmdbSearchResultDto searchMovies(String query, int page) {
        String url = apiUrl + "?apikey=" + apiKey + "&s=" + encodeValue(query) + "&page=" + page;
        logger.debug("Searching movies: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.debug("Response body: {}", response.body());
                return objectMapper.readValue(response.body(), OmdbSearchResultDto.class);
            } else {
                logger.error("Error response: {} {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error searching movies with query {}: {}", query, e.getMessage(), e);
            throw new RuntimeException("Failed to search movies: " + e.getMessage(), e);
        }
    }

    public OmdbSearchResultDto searchMoviesByYear(String query, int year, int page) {
        String url = apiUrl + "?apikey=" + apiKey + "&s=" + encodeValue(query) + "&y=" + year + "&page=" + page;
        logger.debug("Searching movies by year: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.debug("Response body: {}", response.body());
                return objectMapper.readValue(response.body(), OmdbSearchResultDto.class);
            } else {
                logger.error("Error response: {} {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error searching movies with query {} and year {}: {}", query, year, e.getMessage(), e);
            throw new RuntimeException("Failed to search movies by year: " + e.getMessage(), e);
        }
    }

    // Helper method to properly encode URL parameters
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            logger.error("Error encoding value: {}", value);
            return value;
        }
    }
}
