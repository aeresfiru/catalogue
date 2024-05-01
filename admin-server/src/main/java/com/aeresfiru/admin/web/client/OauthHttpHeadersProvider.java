package com.aeresfiru.admin.web.client;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OauthHttpHeadersProvider implements HttpHeadersProvider {

    private static final String REGISTRATION_ID = "keycloak";

    private static final String PRINCIPAL = "admin-service";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public HttpHeaders getHeaders(Instance instance) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient();
        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            return createHeadersWithBearerToken(authorizedClient.getAccessToken());
        }
        return new HttpHeaders();
    }

    private OAuth2AuthorizedClient getAuthorizedClient() {
        return authorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID)
                        .principal(PRINCIPAL)
                        .build());
    }

    private HttpHeaders createHeadersWithBearerToken(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        return headers;
    }
}
