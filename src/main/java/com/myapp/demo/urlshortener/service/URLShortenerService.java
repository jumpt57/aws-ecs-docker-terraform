package com.myapp.demo.urlshortener.service;

import com.myapp.demo.urlshortener.dao.URLShortener;
import com.myapp.demo.urlshortener.exception.URLGenerationErrorException;
import com.myapp.demo.urlshortener.exception.URLNotFoundException;
import com.myapp.demo.urlshortener.repository.URLShortenerRepository;
import com.myapp.demo.urlshortener.utils.Base62;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

/**
 * Service to manage generation of short ID and get back URL
 */
@Service
public class URLShortenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLShortenerService.class);

    @Value("${shortener.maxlength}")
    public Integer MAX_ID_LENGTH;
    @Value("${shortener.maxtry}")
    public Integer MAX_ID_TRY;
    private static final int BASE_ID=62;
    private static long RAND_UPPER_RANGE;
    private static Random rand = new SecureRandom();

    @Autowired
    private URLShortenerRepository urlShortenerRepository;

    /**
     * Init the random generator and the max size of ID
     */
    @PostConstruct
    public void init() {
        rand = new SecureRandom();
        RAND_UPPER_RANGE = (long) Math.pow(BASE_ID, MAX_ID_LENGTH);
    }

    /**
     * Method to generate a tiny ID and associate it to a URL
     * @param url the associate URL to the futur ID
     * @return the short ID
     * @throws URLGenerationErrorException If there is a problem during the ID generation
     */
    public String shortenURL(String url) throws URLGenerationErrorException {
        String id = null;

        //TODO : Use a BLOOM Filter like Rebloom to optimize check of availability
        URLShortener urlObj = urlShortenerRepository.findByUrl(url);
        //if the URL is not already associate to a ID
        if (urlObj==null) {
            //We try to generate a unused random ID for the new URL
            for (int i = 0; i < MAX_ID_TRY; i++) {
                String idtmp = Base62.fromBase10((long) (rand.nextDouble() * RAND_UPPER_RANGE));
                //TODO : Use a BLOOM Filter like Rebloom to optimize check of availability
                if (!urlShortenerRepository.findById(idtmp).isPresent()) {
                    id=idtmp;
                    break;
                }
            }
            //if we exceed the generation ID tries
            if(id==null){
                throw new URLGenerationErrorException();
            }

            LOGGER.info("shortenURL "+ id+" from "+ url);
            urlObj = new URLShortener(id, url);
            try {
                urlShortenerRepository.save(urlObj);
            }catch (Exception e){
                LOGGER.error("Error on save", e);
                id=null;
            }
        } else {
            //If we already have a short id associate to the URL
            id = urlObj.getId();
        }
        return id;
    }

    /**
     * Method to get the original URL from a id
     * @param id the id
     * @return the original URL
     * @throws URLNotFoundException if there is no URL associate to the ID
     */
    public String getURLFromID(String id) throws URLNotFoundException {
        String originalURL;
        //TODO : Use a BLOOM Filter like Rebloom to optimize check of availability
        Optional urlObjTmp = urlShortenerRepository.findById(id);
        if (urlObjTmp.isPresent()) {
            originalURL=((URLShortener)urlObjTmp.get()).getUrl();
            LOGGER.info("getURLFrom "+ id+ " to "+ originalURL);

        } else {
            LOGGER.error("ERROR on getURLFrom "+ id+" no URL found");
            throw new URLNotFoundException();
        }
        return originalURL;
    }

}
