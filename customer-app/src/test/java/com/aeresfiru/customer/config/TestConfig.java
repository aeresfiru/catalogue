package com.aeresfiru.customer.config;

import com.aeresfiru.customer.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return mock();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return mock();
    }

    @Bean
    @Primary
    public ProductClient mockWebClientProductsClient() {
        return new WebClientProductClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public FavouriteProductClient mockWebClientFavouriteProductsClient() {
        return new WebClientFavouriteProductClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public ProductReviewClient mockWebClientProductReviewsClient() {
        return new WebClientProductReviewClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

}
