package com.aeresfiru.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class RouterController {

    @GetMapping("/")
    public Mono<String> toHomePage() {
        return Mono.just("redirect:/customer/products/list");
    }
}
