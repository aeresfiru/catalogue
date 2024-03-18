package com.aeresfiru.manager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    @Qualifier("productRestClient")
    public RestClient restClient(
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            @Value("${aeresfiru.services.catalogue.username:}") String catalogueUsername,
            @Value("${aeresfiru.services.catalogue.password:}") String cataloguePassword) {
        return RestClient.builder()
                .baseUrl(catalogueBaseUrl)
                .requestInterceptor(new BasicAuthenticationInterceptor(catalogueUsername, cataloguePassword))
                .build();
    }
}
