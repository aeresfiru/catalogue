package com.aeresfiru.catalogue.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OauthMetricsClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${aeresfiru.services.admin.registration-id:keycloak}")
    private String registrationId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id:catalogue-service-metrics-client}")
    private String principalName;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            var authorizedClient = authorizeClient();
            if (authorizedClient != null) {
                request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
            }
        }
        return execution.execute(request, body);
    }

    private OAuth2AuthorizedClient authorizeClient() {
        var oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
                .principal(principalName)
                .build();
        return this.authorizedClientManager.authorize(oAuth2AuthorizeRequest);
    }
}
