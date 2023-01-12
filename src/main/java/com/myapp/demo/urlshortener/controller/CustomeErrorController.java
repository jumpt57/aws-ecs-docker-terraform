package com.myapp.demo.urlshortener.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle error on server
 */
@Controller
public class CustomeErrorController implements ErrorController {

    /**
     * Default error page
     * @return error.html
     */
    @RequestMapping("/error")
    public String handleError() {
        return "error.html";
    }

    /**
     * Error page if a tiny URL is not found or expired
     * @return errorNotFound.html
     */
    @RequestMapping(value = "/notfound")
    public String errorURLNotFound()  {
        return "errorNotFound.html";
    }

    /**
     * Error page if there is unexpected request
     * @return errorTeapot.html
     */
    @RequestMapping(value = "/teapot")
    public String errorTeapot()  {
        return "errorTeapot.html";
    }
}