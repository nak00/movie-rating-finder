package com.example.movieratingfinder.repository;

import com.example.movieratingfinder.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByImdbId(String imdbId);

    List<Movie> findByNameContainingIgnoreCase(String name);
    Page<Movie> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Methods for valid poster filtering
    List<Movie> findByPosterValidTrue(Sort sort);
    Page<Movie> findByPosterValidTrue(Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.posterValid = false OR m.posterValid IS NULL ORDER BY m.name")
    List<Movie> findByPosterValidFalseOrPosterValidIsNull(Sort sort);

    Page<Movie> findByPosterValidFalseOrPosterValidIsNull(Pageable pageable);

    // Methods for search with valid poster filtering
    @Query("SELECT m FROM Movie m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.posterValid = true")
    List<Movie> findByNameContainingIgnoreCaseAndPosterValidTrue(String name, Sort sort);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND (m.posterValid = false OR m.posterValid IS NULL)")
    List<Movie> findByNameContainingIgnoreCaseAndPosterValidFalseOrPosterValidIsNull(String name, Sort sort);

    // Methods for year filtering with valid poster filtering
    @Query("SELECT m FROM Movie m WHERE m.year = :year AND m.posterValid = true")
    List<Movie> findByYearAndPosterValidTrue(int year, Sort sort);

    @Query("SELECT m FROM Movie m WHERE m.year = :year AND (m.posterValid = false OR m.posterValid IS NULL)")
    List<Movie> findByYearAndPosterValidFalseOrPosterValidIsNull(int year, Sort sort);

    // Original methods
    List<Movie> findTop10ByOrderByImdbRatingDesc();
    Page<Movie> findByYear(int year, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.posterValid = false OR m.posterValid IS NULL")
    List<Movie> findMoviesWithInvalidPosters();
}
