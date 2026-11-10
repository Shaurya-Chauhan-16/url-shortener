package com.urlshortener.repository;

import com.urlshortener.model.UrlMapping;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public UrlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Saves a URL mapping and returns its generated ID.
    public Long save(String shortCode, String originalUrl) {
        String sql = "INSERT INTO urls (short_code, original_url) VALUES (?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, Long.class, shortCode, originalUrl);
    }

    // Finds a URL mapping by its short code.
    public Optional<UrlMapping> findByShortCode(String shortCode) {
        String sql = "SELECT * FROM urls WHERE short_code = ?";
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(UrlMapping.class),
                shortCode
        ).stream().findFirst();
    }

    // Checks whether a short code exists.
    public boolean existsByShortCode(String shortCode) {
        String sql = "SELECT EXISTS (SELECT 1 FROM urls WHERE short_code = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, shortCode);
        return exists != null && exists;
    }

    // Increments the click count for a short code.
    public int incrementClickCount(String shortCode) {
        String sql = "UPDATE urls SET click_count = click_count + 1 WHERE short_code = ?";
        return jdbcTemplate.update(sql, shortCode);
    }
}
