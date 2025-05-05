package com.example.movieratingfinder.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeDatabase() {
        logger.info("Enabling all existing user accounts...");
        int updatedRows = jdbcTemplate.update("UPDATE users SET enabled = true WHERE enabled = false");
        logger.info("Enabled {} existing user accounts", updatedRows);
    }
}
