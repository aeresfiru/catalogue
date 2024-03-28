package com.aeresfiru.customer.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Qualifier("productWebClient")
    public WebClient productWebClient(
            @Value("${aeresfiru.services.catalogue.url:http://localhost:8081}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @Qualifier("feedbackWebClient")
    public WebClient feedbackWebClient(
            @Value("${aeresfiru.services.feedback.url:http://localhost:8085}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
