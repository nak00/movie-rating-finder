package com.example.movieratingfinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OmdbSearchResultDto {

    @JsonProperty("Search")
    private List<OmdbSearchItemDto> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    // Getters and setters
    public List<OmdbSearchItemDto> getSearch() {
        return search;
    }

    public void setSearch(List<OmdbSearchItemDto> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return "True".equalsIgnoreCase(response);
    }

    public int getTotalResultsAsInt() {
        try {
            return Integer.parseInt(totalResults);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
