package com.example.blog.controller;

import com.example.blog.service.BlogService;
import com.example.blog.service.BlogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController implements ErrorController {
    private BlogServiceImpl blogService;
    @Autowired
    public ErrorPageController(BlogServiceImpl blogService) {
        this.blogService = blogService;
    }
    @Override
    public String getErrorPath() {
        return "myErrorPage";
    }
    @GetMapping("/myErrorPage")
    public String getErrorPage(Model model, Authentication auth) {
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        model.addAttribute("header_title", "ERROR 404");
        return "errorPage";
    }
}
