package com.myapp.demo.urlshortener.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON model for request and response of URLShortener generation
 */
public class URLShortenerJSON {
    private String url;

    @JsonCreator
    public URLShortenerJSON(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
