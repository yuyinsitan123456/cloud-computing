package com.aca.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletResponse response) {
        return "sentiment";
    }

    @RequestMapping(value = "/sentiment", method = RequestMethod.GET)
    public String getSentiment(HttpServletResponse response) {
        return "sentiment";
    }

    @RequestMapping(value = "/postcode", method = RequestMethod.GET)
    public String getPostcode(HttpServletResponse response) {
        return "postcode";
    }
}
