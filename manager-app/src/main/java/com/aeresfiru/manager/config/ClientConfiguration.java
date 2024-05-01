package com.aeresfiru.manager.config;

import com.aeresfiru.manager.security.OauthMetricsClientHttpRequestInterceptor;
import com.aeresfiru.manager.security.OauthServiceClientHttpRequestInterceptor;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {

    @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}")
    private String catalogueBaseUri;

    @Bean
    public RestClient restClient(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);

        var interceptor = new OauthServiceClientHttpRequestInterceptor(authorizedClientManager);

        return RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(interceptor)
                .build();
    }
    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        var interceptor = new OauthMetricsClientHttpRequestInterceptor(authorizedClientManager);

        return new BlockingRegistrationClient(new RestTemplateBuilder()
                .interceptors(interceptor)
                .build());
    }
}
