package com.halasa.demoshop.app.security.jwt;

import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.halasa.demoshop.app.security.SecurityPrincipal;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JwtVerifierUtils {

    /**
     * Expects the token to have 'name' and 'email' claims.
     */
    public static JwtAuthenticationToken getAuthentication(JWT jwt, String jwtString, JWTClaimsSet claimSet, String role) {
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(role));
        String subject = claimSet.getSubject();
        String email = (String) claimSet.getClaim("email");
        String name = (String) claimSet.getClaim("name");

        Map<String, Object> claims = new HashMap<>();
        claims.put("subject", subject);

        return new JwtAuthenticationToken(new SecurityPrincipal(email, name, authorities), jwtString, claims, authorities);
    }
}
