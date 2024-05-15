package com.aeresfiru.catalogue.config;

import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class ClientConfiguration {

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            OauthMetricsClientHttpRequestInterceptor clientHttpRequestInterceptor
    ) {
        return new BlockingRegistrationClient(new RestTemplateBuilder()
                .interceptors(clientHttpRequestInterceptor)
                .build());
    }

    @Bean
    public OauthMetricsClientHttpRequestInterceptor oauthMetricsClientHttpRequestInterceptor(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        return new OauthMetricsClientHttpRequestInterceptor(authorizedClientManager);
    }
}
