package com.aeresfiru.customer.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Configuration
    @ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "false")
    public static class StandaloneClientConfig {

        @Bean
        @Scope("prototype")
        public WebClient.Builder selmagServicesWebClientBuilder(
                ReactiveClientRegistrationRepository clientRegistrationRepository,
                ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
            ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                    new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                            authorizedClientRepository);
            filter.setDefaultClientRegistrationId("keycloak");
            return WebClient.builder()
                    .filter(filter);
        }
    }

    @Bean
    public ProductClient webClientProductsClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientProductClient(selmagServicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public FavouriteProductClient favouriteProductClient(
            @Value("${aeresfiru.services.feedback.uri:http://localhost:8085}") String feedbackBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientFavouriteProductClient(selmagServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public ProductReviewClient productReviewClient(
            @Value("${aeresfiru.services.feedback.uri:http://localhost:8085}") String feedbackBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientProductReviewClient(selmagServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
