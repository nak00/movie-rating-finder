package com.example.movieratingfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableCaching
@SpringBootApplication
public class MovieRatingFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieRatingFinderApplication.class, args);
    }

}
