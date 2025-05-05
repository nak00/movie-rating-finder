package com.example.movieratingfinder.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String posterUrl;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean posterValid = false;

    @Column(name = "release_year")
    private int year; // 'year' is mapped to 'release_year' in DB

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "imdb_id")
    private String imdbId;

    private double imdbRating;
    private double rottenTomatoesRating;

    @ManyToMany(mappedBy = "watchlist")
    private Set<User> users = new HashSet<>();

    // Constructors
    public Movie() {}

    public Movie(String name, String posterUrl, int year, String description, double imdbRating, double rottenTomatoesRating) {
        this.name = name;
        this.posterUrl = posterUrl;
        this.year = year;
        this.description = description;
        this.imdbRating = imdbRating;
        this.rottenTomatoesRating = rottenTomatoesRating;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public boolean isPosterValid() { return posterValid; }
    public void setPosterValid(boolean posterValid) { this.posterValid = posterValid; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getImdbRating() { return imdbRating; }
    public void setImdbRating(double imdbRating) { this.imdbRating = imdbRating; }

    public double getRottenTomatoesRating() { return rottenTomatoesRating; }
    public void setRottenTomatoesRating(double rottenTomatoesRating) { this.rottenTomatoesRating = rottenTomatoesRating; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
