package com.aeresfiru.customer.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder scServicesClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        var filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    @Qualifier("productWebClient")
    public WebClient productWebClient(
            WebClient.Builder scServicesClientBuilder,
            @Value("${aeresfiru.services.catalogue.url:http://localhost:8081}") String baseUrl) {
        return scServicesClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @Qualifier("feedbackWebClient")
    public WebClient feedbackWebClient(
            WebClient.Builder scServicesClientBuilder,
            @Value("${aeresfiru.services.feedback.url:http://localhost:8085}") String baseUrl) {
        return scServicesClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
}
