package com.halasa.demoshop.app.security;

import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.service.BasicException;
import com.halasa.demoshop.service.ConfigurationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Component
public class JwtGenerator {

    private final JWSSigner jwsSigner;
    private final String issuer;

    public JwtGenerator(
            @Value("${security.localJwt.rsa.private-key-file}") String privateKeyFile,
            @Value("${security.localJwt.issuer}") String issuer) {
        this.issuer = issuer;
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyFile));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(spec);
            jwsSigner = new RSASSASigner(privateKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new ConfigurationException("Error reading RSA private key from file '" + privateKeyFile + "' for signing JWTs.", ex);
        }
    }

    public String generateJwt(String subject, String name, String email) {
        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("name", name)
                .claim("email", email)
                .issuer(this.issuer)
                .audience(this.issuer)
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                claimsSet);

        // Compute the RSA signature
        try {
            signedJwt.sign(jwsSigner);
        } catch (JOSEException ex) {
            throw new BasicException(ErrorCode.SYSTEM_FAILURE, "Error siging a JWT", ex);
        }

        return signedJwt.serialize();
    }
}
