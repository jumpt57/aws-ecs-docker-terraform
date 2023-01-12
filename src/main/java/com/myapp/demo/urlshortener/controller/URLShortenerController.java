package com.myapp.demo.urlshortener.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.demo.urlshortener.exception.URLGenerationErrorException;
import com.myapp.demo.urlshortener.exception.URLNotFoundException;
import com.myapp.demo.urlshortener.model.URLShortenerJSON;
import com.myapp.demo.urlshortener.service.URLShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;


/**
 * Main controller to manage request for short url
 */
@Controller
public class URLShortenerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLShortenerController.class);

    @Value("${shortener.maxlength}")
    public Integer MAX_ID_LENGTH;

    @Autowired
    URLShortenerService urlShortenerService;

    /**
     * POST Method to generate a short URL from a URL
     * @param urlShortenerJSON JSON object containing the URL
     * @param request requestservlet to get the server host
     * @return JSON with the short URL ("host/r/ID") @see com.myapp.demo.urlshortener.model.URLShortenerJSON
     * specific ERROR HTTP code :
     * 400 : For invalid original URL
     * 422 : For a problem in generation of the short ID
     *
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = {"application/json"})
    public ResponseEntity<String> URLToShorten(@RequestBody @Valid final URLShortenerJSON urlShortenerJSON, HttpServletRequest request) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        String requestedUrl = urlShortenerJSON.getUrl();
        LOGGER.info("Request URLToShorten : " + requestedUrl);
        try {

            //Generation of the short URL
            String shortID = urlShortenerService.shortenURL(requestedUrl);
            if (shortID != null && !shortID.isEmpty()) {
                //if short ID is well generated => generate full short url
                String localURL = getURLWithContextPath(request);
                URLShortenerJSON respJson = new URLShortenerJSON(localURL + "/r/" + shortID);
                LOGGER.info("Shortened url to: " + respJson.getUrl());
                response = new ResponseEntity<>(new ObjectMapper().writeValueAsString(respJson), HttpStatus.OK);
            }
        } catch (ValidationException e) {
            LOGGER.error("Invalid_URLShortener ", e);
            response = new ResponseEntity<>("Invalid_URLShortener", HttpStatus.BAD_REQUEST);
        } catch (URLGenerationErrorException e) {
            LOGGER.error("URL_generator_error ", e);
            response = new ResponseEntity<>("URL_generator_error", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonProcessingException ", e);
            response = new ResponseEntity<>("Internal_Error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return response;
    }

    /**
     * GET method to redirect to the original URL from a short URL ID
     * @param id ID of the short URL in BASE62
     * @return redirect to the original URL or Error page
     */
    @RequestMapping(value = "/r/{id}", method = RequestMethod.GET)
    public RedirectView ShortenToURL(@PathVariable String id) {
        LOGGER.info("Request ShortToURL: " + id);
        RedirectView redirectView = new RedirectView();
        String redirectUrlString;
        if (id.length() > MAX_ID_LENGTH) {
            LOGGER.error("id too long");
            redirectUrlString = "../teapot";
        } else {
            try {
                redirectUrlString = urlShortenerService.getURLFromID(id);
                LOGGER.info("Original URLShortener: " + redirectUrlString);
            } catch (URLNotFoundException e) {
                redirectUrlString = "../notfound";
            }
        }
        redirectView.setUrl(redirectUrlString);
        return redirectView;
    }

    /**
     * GET method index
     * @return index page
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home() {
        return "index.html";
    }

    /**
     * Function to get the server host url from a servlet request
     * @param request resquestServlet
     * @return URL base of the server host
     */
    private static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
