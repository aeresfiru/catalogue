package com.aeresfiru.feedback.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;

import java.io.IOException;

@RequiredArgsConstructor
public class EurekaHttpRequestInterceptor implements HttpRequestInterceptor {

    private static final String REGISTRATION_ID = "discovery";

    private static final String PRINCIPAL = "feedback-service";

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public void process(HttpRequest request, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
        if (!request.containsHeader(HttpHeaders.AUTHORIZATION)) {
            OAuth2AuthorizedClient authorizedClient = authorizeClient();
            if (authorizedClient != null) {
                String token = "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue());
                request.setHeader(HttpHeaders.AUTHORIZATION, token);
            }
        }
    }

    private OAuth2AuthorizedClient authorizeClient() {
        OAuth2AuthorizeRequest request = buildRequest();
        return authorizedClientManager.authorize(request)
                .block();
    }

    private static OAuth2AuthorizeRequest buildRequest() {
        return OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID)
                .principal(PRINCIPAL)
                .build();
    }
}
