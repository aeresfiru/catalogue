package com.aeresfiru.manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/errors/403")
    public String handleForbidden(HttpServletResponse response) {
        return "errors/403";
    }
}
