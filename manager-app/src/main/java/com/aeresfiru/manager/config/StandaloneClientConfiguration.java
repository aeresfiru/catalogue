package com.aeresfiru.manager.config;

import com.aeresfiru.manager.security.OauthServiceClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "false")
public class StandaloneClientConfiguration {

    @Bean
    public RestClient catalogueRestClient(
            OauthServiceClientHttpRequestInterceptor serviceClientHttpRequestInterceptor,
            @Value("${aeresfiru.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri
    ) {
        return RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(serviceClientHttpRequestInterceptor)
                .build();
    }
}
