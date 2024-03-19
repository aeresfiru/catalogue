package com.aeresfiru.manager.config;

import com.aeresfiru.manager.security.OauthClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    @Qualifier("productRestClient")
    public RestClient restClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            @Value("${aeresfiru.services.catalogue.registration-id:keycloak}") String registrationId,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        var authorizedClientManager = getAuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        return RestClient.builder()
                .baseUrl(catalogueBaseUrl)
                .requestInterceptor(new OauthClientHttpRequestInterceptor(authorizedClientManager, registrationId))
                .build();
    }

    private static DefaultOAuth2AuthorizedClientManager getAuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        return new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    }
}
