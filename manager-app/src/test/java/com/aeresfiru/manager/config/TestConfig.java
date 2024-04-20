package com.aeresfiru.manager.config;

import com.aeresfiru.manager.client.RequestExecutor;
import com.aeresfiru.manager.client.RestClientProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return mock(OAuth2AuthorizedClientRepository.class);
    }

    @Bean
    @Primary
    public RestClientProductClient testProductRestClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:54321}") String catalogueBaseUri,
            RequestExecutor requestExecutor
    ) {
        var client = (HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build());
        var restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(client))
                .baseUrl(catalogueBaseUri)
                .build();
        return new RestClientProductClient(restClient, requestExecutor);
    }
}
