package com.halasa.demoshop.app.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 45462494L;

    private final SecurityPrincipal principal;
    private final Map<String, Object> claims;
    private final String jwtString;

    public JwtAuthenticationToken(SecurityPrincipal principal, String jwtString, Map<String, Object> jwtClaims,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.claims = Collections.unmodifiableMap(jwtClaims);
        super.setAuthenticated(true);
        this.jwtString = jwtString;
    }

    @Override
    public Object getCredentials() {
        return jwtString;
    }

    @Override
    public SecurityPrincipal getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public String getJwtString() {
        return jwtString;
    }
}
