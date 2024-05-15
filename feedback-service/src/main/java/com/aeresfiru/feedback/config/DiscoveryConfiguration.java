package com.aeresfiru.feedback.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

import java.util.Collections;

@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", havingValue = "true")
public class DiscoveryConfiguration {

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties restTemplateTimeoutProperties,
            EurekaHttpRequestInterceptor httpRequestInterceptor
    ) {
        return new DefaultEurekaClientHttpRequestFactorySupplier(
                restTemplateTimeoutProperties, Collections.singletonList(httpRequestInterceptor));
    }

    @Bean
    public EurekaHttpRequestInterceptor eurekaHttpRequestInterceptor(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        return new EurekaHttpRequestInterceptor(authorizedClientManager);
    }
}