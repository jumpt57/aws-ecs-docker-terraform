package com.myapp.demo.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.demo.urlshortener.controller.URLShortenerController;
import com.myapp.demo.urlshortener.exception.URLGenerationErrorException;
import com.myapp.demo.urlshortener.exception.URLNotFoundException;
import com.myapp.demo.urlshortener.model.URLShortenerJSON;
import com.myapp.demo.urlshortener.service.URLShortenerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(URLShortenerController.class)
@ActiveProfiles(profiles = "test")
public class URLShortenerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private URLShortenerService service;

    private static final String HOST_URL="http://localhost:80/r/";
    private static final String GOOD_URL="https://start.spring.io/";
    private static final String TEST_ID="aaaa";

    @Test
    public void URLToShorten_OK() throws Exception {

        URLShortenerJSON input = new URLShortenerJSON(GOOD_URL);

        Mockito.when(service.shortenURL(Mockito.any(String.class))).thenReturn(TEST_ID);

        mvc.perform(MockMvcRequestBuilders.post("/")
                .content(asJsonString(input))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(HOST_URL+TEST_ID));
    }


    @Test
    public void URLToShorten_KO_NO_ID_AVAILABLE() throws Exception {

        URLShortenerJSON input = new URLShortenerJSON(GOOD_URL);

        Mockito.when(service.shortenURL(Mockito.any(String.class))).thenThrow(new URLGenerationErrorException());

        mvc.perform(MockMvcRequestBuilders.post("/")
                .content(asJsonString(input))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void ShortenToURL_OK() throws Exception {

        Mockito.when(service.getURLFromID(Mockito.any(String.class))).thenReturn(GOOD_URL);

        mvc.perform(MockMvcRequestBuilders.get("/r/"+TEST_ID))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GOOD_URL));
    }

    @Test
    public void ShortenToURL_KO_NOT_GEN() throws Exception {

        Mockito.when(service.getURLFromID(Mockito.any(String.class))).thenThrow(new URLNotFoundException());

        mvc.perform(MockMvcRequestBuilders.get("/r/pouet"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("../notfound"));
    }

    @Test
    public void ShortenToURL_KO_INPUT_TOO_LONG() throws Exception {

        Mockito.when(service.getURLFromID(Mockito.any(String.class))).thenThrow(new URLNotFoundException());

        mvc.perform(MockMvcRequestBuilders.get("/r/1234567891011"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("../teapot"));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
