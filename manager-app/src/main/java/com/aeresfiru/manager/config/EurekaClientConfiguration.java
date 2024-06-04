package com.aeresfiru.manager.config;

import com.aeresfiru.manager.security.OauthServiceClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", havingValue = "true", matchIfMissing = true)
public class EurekaClientConfiguration {

    @Bean
    public RestClient catalogueRestClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            OauthServiceClientHttpRequestInterceptor interceptor,
            LoadBalancerClient loadBalancerClient) {
        return RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(new LoadBalancerInterceptor(loadBalancerClient))
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties restTemplateTimeoutProperties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                        authorizedClientService);

        return new DefaultEurekaClientHttpRequestFactorySupplier(restTemplateTimeoutProperties, List.of(
                (request, entity, context) -> {
                    if (!request.containsHeader(HttpHeaders.AUTHORIZATION)) {
                        OAuth2AuthorizedClient authorizedClient = authorizedClientManager
                                .authorize(OAuth2AuthorizeRequest
                                        .withClientRegistrationId("discovery")
                                        .principal("manager-app")
                                        .build());

                        request.setHeader(HttpHeaders.AUTHORIZATION,
                                "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()));
                    }
                }
        ));
    }
}
