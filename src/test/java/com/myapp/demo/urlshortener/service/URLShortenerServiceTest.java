package com.myapp.demo.urlshortener.service;

import com.myapp.demo.urlshortener.dao.URLShortener;
import com.myapp.demo.urlshortener.exception.URLGenerationErrorException;
import com.myapp.demo.urlshortener.exception.URLNotFoundException;
import com.myapp.demo.urlshortener.repository.URLShortenerRepository;
import com.myapp.demo.urlshortener.service.URLShortenerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
//@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.2")
@ActiveProfiles(profiles = "test")
public class URLShortenerServiceTest {
    private static final String GOOD_URL="http://google.fr";
    private static final String TEST_ID="aaaa";
    @Autowired
    private URLShortenerService service;

    @MockBean
    private URLShortenerRepository repository;


    @Test
    public void shortenURL_OK_NEW() throws URLGenerationErrorException {
        Mockito.when(repository.findByUrl(Mockito.any(String.class))).thenReturn(null);
        Mockito.when(repository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());
        assertFalse(service.shortenURL(GOOD_URL).isEmpty());
    }

    @Test
    public void shortenURL_OK_AlreadyExisting() throws URLGenerationErrorException {
        URLShortener resp = new URLShortener(TEST_ID,GOOD_URL);
        Mockito.when(repository.findByUrl(Mockito.any(String.class))).thenReturn(resp);
        assertEquals(service.shortenURL(GOOD_URL),TEST_ID);
    }

    @Test
    public void shortenURL_KO_RANDOM_GEN() throws URLGenerationErrorException {
        URLShortener resp = new URLShortener(TEST_ID,GOOD_URL);
        Mockito.when(repository.findByUrl(Mockito.any(String.class))).thenReturn(null);
        Mockito.when(repository.findById(Mockito.any(String.class))).thenReturn(Optional.of(resp));
        
        assertThrows(URLGenerationErrorException.class, () -> service.shortenURL(GOOD_URL));
    }

    @Test
    public void shortenURL_KO_DB_DEAD_ON_SAVE() throws URLGenerationErrorException {
        Mockito.when(repository.findByUrl(Mockito.any(String.class))).thenReturn(null);
        Mockito.when(repository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any(URLShortener.class))).thenThrow(new RuntimeException("Mongo DEAD"));
        assertNull(service.shortenURL(GOOD_URL));
    }

    @Test
    public void getURLFromID_OK() throws URLNotFoundException {
        URLShortener resp = new URLShortener(TEST_ID,GOOD_URL);
        Mockito.when(repository.findById(Mockito.any(String.class))).thenReturn(Optional.of(resp));
        assertEquals(service.getURLFromID(TEST_ID),GOOD_URL);
    }

    @Test
    public void getURLFromID_KO_NOT_FOUND() throws URLNotFoundException {
        Mockito.when(repository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());
        assertThrows(URLNotFoundException.class, () -> service.getURLFromID(TEST_ID));
    }
}
