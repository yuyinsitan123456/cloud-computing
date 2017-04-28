package com.aca.controller;

import com.aca.dao.User;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String test(HttpServletResponse response) throws IOException {
        return "home";
    }
    
    @ResponseBody
    @RequestMapping(value = "/scenario/1")
    public User testJsonBean(HttpServletResponse response) throws IOException {
        return new User();
    }
}
