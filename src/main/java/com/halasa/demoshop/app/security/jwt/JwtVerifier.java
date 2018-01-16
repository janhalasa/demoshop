package com.halasa.demoshop.app.security.jwt;

import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

public interface JwtVerifier {

    boolean acceptsIssuer(String issuer);

    void validate(JWT jwt, String idTokenString, JWTClaimsSet claimsSet);

    JwtAuthenticationToken getAuthentication(JWT jwt, String jwtString, JWTClaimsSet claimSet);
}
