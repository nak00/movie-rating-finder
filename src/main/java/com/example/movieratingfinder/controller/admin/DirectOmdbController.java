package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.model.Movie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/direct-omdb")
public class DirectOmdbController {

    private static final Logger logger = LoggerFactory.getLogger(DirectOmdbController.class);
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public DirectOmdbController(@Value("${omdb.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        return "admin/omdb-search";
    }

    @PostMapping("/search")
    public String search(@RequestParam String title, Model model) {
        try {
            logger.info("Searching OMDb for title: {}", title);

            // URL encode the title
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            String urlString = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + encodedTitle;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                logger.info("Response: {}", responseBody);

                // Parse JSON response
                JsonNode rootNode = objectMapper.readTree(responseBody);

                if ("True".equals(rootNode.path("Response").asText())) {
                    Movie movie = new Movie();
                    movie.setName(rootNode.path("Title").asText());
                    movie.setImdbId(rootNode.path("imdbID").asText());

                    // Parse year
                    try {
                        String yearStr = rootNode.path("Year").asText();
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

                    // Parse IMDb rating
                    try {
                        String ratingStr = rootNode.path("imdbRating").asText();
                        if (ratingStr != null && !ratingStr.equals("N/A")) {
                            movie.setImdbRating(Double.parseDouble(ratingStr));
                        } else {
                            movie.setImdbRating(0.0);
                        }
                    } catch (NumberFormatException e) {
                        movie.setImdbRating(0.0);
                    }

                    // Set other fields
                    movie.setDescription(rootNode.path("Plot").asText());
                    movie.setPosterUrl(rootNode.path("Poster").asText());

                    // Calculate RT rating from IMDb rating
                    int rtRating = (int) Math.min(100, Math.round(movie.getImdbRating() * 10));
                    movie.setRottenTomatoesRating(rtRating);

                    model.addAttribute("movie", movie);
                    model.addAttribute("searchSuccess", true);
                    logger.info("Found movie: {}", movie.getName());
                } else {
                    model.addAttribute("searchError", "Movie not found");
                    logger.info("Movie not found: {}", title);
                }
            } else {
                model.addAttribute("searchError", "Error from OMDb API: " + responseCode);
                logger.error("Error response from API: {}", responseCode);
            }
        } catch (Exception e) {
            model.addAttribute("searchError", "Error searching for movie: " + e.getMessage());
            logger.error("Error searching for movie: {}", e.getMessage(), e);
        }

        return "admin/omdb-search";
    }

    @PostMapping("/import")
    public String importMovie(@ModelAttribute Movie movie, @Value("${omdb.api.key}") String apiKey) {
        // Implementation for importing the movie
        // This would be similar to your existing import functionality
        return "redirect:/admin/movies";
    }
}
