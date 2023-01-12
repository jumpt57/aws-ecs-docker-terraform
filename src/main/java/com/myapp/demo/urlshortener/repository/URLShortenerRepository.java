package com.myapp.demo.urlshortener.repository;

import com.myapp.demo.urlshortener.dao.URLShortener;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository to manage URLShortener
 */
@Repository
public interface URLShortenerRepository extends JpaRepository<URLShortener, String> {
    URLShortener findByUrl(String Url);
}
