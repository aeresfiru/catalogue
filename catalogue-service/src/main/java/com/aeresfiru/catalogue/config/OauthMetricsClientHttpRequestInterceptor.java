package com.aeresfiru.catalogue.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;

@RequiredArgsConstructor
public class OauthMetricsClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private static final String REGISTRATION_ID = "metrics";

    private static final String PRINCIPAL_NAME = "catalogue-service-metrics-client";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            var authorizedClient = authorizeClient();
            if (authorizedClient != null) {
                String token = authorizedClient.getAccessToken().getTokenValue();
                request.getHeaders().setBearerAuth(token);
            }
        }
        return execution.execute(request, body);
    }

    private OAuth2AuthorizedClient authorizeClient() {
        var oAuth2AuthorizeRequest = buildAuthorizeRequest();
        return this.authorizedClientManager.authorize(oAuth2AuthorizeRequest);
    }

    private static OAuth2AuthorizeRequest buildAuthorizeRequest() {
        return OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID)
                .principal(PRINCIPAL_NAME)
                .build();
    }
}
