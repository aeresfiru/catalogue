package com.aeresfiru.manager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OauthMetricsClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    private static final String REGISTRATION_ID = "metrics";

    private static final String PRINCIPAL = "manager-app-metrics-client";

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
        var oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID)
                .principal(PRINCIPAL)
                .build();

        return this.authorizedClientServiceOAuth2AuthorizedClientManager.authorize(oAuth2AuthorizeRequest);
    }
}
