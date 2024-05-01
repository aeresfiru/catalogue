package com.aeresfiru.manager.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DefaultOidcUserService extends OidcUserService {

    private static final String GROUPS = "groups";

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oidcUser = super.loadUser(userRequest);
        var authorities = getGrantedAuthorities(oidcUser);
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private static List<? extends GrantedAuthority> getGrantedAuthorities(OidcUser oidcUser) {
        var roles = Optional.ofNullable(oidcUser.getClaimAsStringList(GROUPS))
                .orElseGet(LinkedList::new).stream()
                .filter(role -> role.startsWith(ROLE_PREFIX))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return Stream.concat(roles.stream(), oidcUser.getAuthorities().stream())
                .toList();
    }
}
