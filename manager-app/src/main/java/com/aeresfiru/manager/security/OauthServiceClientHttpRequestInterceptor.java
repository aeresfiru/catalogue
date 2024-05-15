package com.aeresfiru.manager.security;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OauthServiceClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager;

    private static final String REGISTRATION_ID = "keycloak";

    @Setter
    private SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            OAuth2AuthorizedClient auth = authorizeClient();
            if (auth != null) {
                request.getHeaders().setBearerAuth(auth.getAccessToken().getTokenValue());
            }
        }
        return execution.execute(request, body);
    }

    private OAuth2AuthorizedClient authorizeClient() {
        var request = OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID)
                .principal(this.securityContextHolderStrategy.getContext().getAuthentication())
                .build();

        return this.defaultOAuth2AuthorizedClientManager.authorize(request);
    }
}
