package com.aeresfiru.customer.config;

import com.aeresfiru.customer.client.*;
import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ReactiveRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
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
        public WebClient.Builder servicesWebClientBuilder(
                ReactiveClientRegistrationRepository clientRegistrationRepository,
                ServerOAuth2AuthorizedClientRepository authorizedClientRepository
        ) {
            var filterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                    clientRegistrationRepository, authorizedClientRepository);
            filterFunction.setDefaultClientRegistrationId("keycloak");

            return WebClient.builder()
                    .filter(filterFunction);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "true", matchIfMissing = true)
    public static class CloudClientConfig {
        @Bean
        @Scope("prototype")
        @LoadBalanced
        public WebClient.Builder servicesWebClientBuilder(
                ReactiveClientRegistrationRepository clientRegistrationRepository,
                ServerOAuth2AuthorizedClientRepository authorizedClientRepository
        ) {
            var filterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                    clientRegistrationRepository, authorizedClientRepository);
            filterFunction.setDefaultClientRegistrationId("keycloak");

            return WebClient.builder()
                    .filter(filterFunction);
        }
    }

    @Bean
    public ProductClient webClientProductsClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder servicesWebClientBuilder
    ) {
        return new WebClientProductClient(servicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public FavouriteProductClient webClientFavouriteProductsClient(
            @Value("${aeresfiru.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder servicesWebClientBuilder
    ) {
        return new WebClientFavouriteProductClient(servicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public ProductReviewClient webClientProductReviewsClient(
            @Value("${aeresfiru.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder servicesWebClientBuilder
    ) {
        return new WebClientProductReviewClient(servicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService,
            ClientProperties clientProperties
    ) {
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        var filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        filter.setDefaultClientRegistrationId("metrics");

        var webClient = WebClient.builder()
                .filter(filter)
                .build();

        return new ReactiveRegistrationClient(webClient, clientProperties.getReadTimeout());
    }
}
