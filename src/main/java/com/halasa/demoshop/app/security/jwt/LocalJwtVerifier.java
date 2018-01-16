package com.halasa.demoshop.app.security.jwt;

import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.service.ConfigurationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;

@Component
public class LocalJwtVerifier implements JwtVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalJwtVerifier.class);

    private final JWSVerifier jwsVerifier;
    private final String issuer;

    public LocalJwtVerifier(
            @Value("${security.localJwt.issuer}") String issuer,
            @Value("${security.localJwt.rsa.public-key-file}") String publicKeyFile) {
        this.issuer = issuer;

        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyFile));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

            this.jwsVerifier = new RSASSAVerifier(publicKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new ConfigurationException("Error reading RSA public key from file '" + publicKeyFile + "' for verifying JWTs.", ex);
        }
    }

    @Override
    public boolean acceptsIssuer(String issuer) {
        return this.issuer.equals(issuer);
    }

    @Override
    public void validate(JWT idToken, String jwtString, JWTClaimsSet claimsSet) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(jwtString);
            if (! signedJwt.verify(this.jwsVerifier)) {
                throw new BadCredentialsException("Invalid JWT: " + jwtString);
            }
        } catch (ParseException | JOSEException ex) {
            throw new BadCredentialsException("Invalid JWT: " + jwtString, ex);
        }
    }

    @Override
    public JwtAuthenticationToken getAuthentication(JWT jwt, String jwtString, JWTClaimsSet claimSet) {
        return JwtVerifierUtils.getAuthentication(jwt, jwtString, claimSet, Roles.ADMIN);
    }
}
