package com.halasa.demoshop.rest.controller;

import com.halasa.demoshop.api.AuthRestPaths;
import com.halasa.demoshop.api.dto.response.JwtAuthenticationResponse;
import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.halasa.demoshop.app.security.JwtGenerator;
import com.halasa.demoshop.app.security.SecurityPrincipal;
import com.halasa.demoshop.service.RevokedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class JwtAuthenticationController {

    private final JwtGenerator jwtGenerator;
    private final RevokedTokenService revokedTokenService;

    @Autowired
    public JwtAuthenticationController(
            JwtGenerator jwtGenerator,
            RevokedTokenService revokedTokenService) {
        this.jwtGenerator = jwtGenerator;
        this.revokedTokenService = revokedTokenService;
    }

    /**
     * Authenticated by Spring security - requires a special role, just to make sure that the request passed the authentication.
     */
    @PreAuthorize("hasRole('TOKEN')")
    @PostMapping(path = AuthRestPaths.TOKEN,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse createToken(Principal user) {
        // This JWT is created with fixed name and email, because the basic HTTP auth filter is just that simple.
        // In a real application, it would be better to extend the authentication filter and get a more complete principal here.
        return new JwtAuthenticationResponse(this.jwtGenerator.generateJwt(user.getName(), "Pedro Vedro", "pedro@ved.ro"));
    }

    /**
     * Token renewal is useful for long running applications, so the user doesn't have to enter credentials every time the token expires.
     * This endpoint can also be used to exchange an external ID token for a local JWT. It can come handy for client applications,
     * so they don't need to handle different kinds of tokens and renewal strategies.
     */
    @PostMapping(path = AuthRestPaths.TOKEN_RENEW,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse renewToken(@AuthenticationPrincipal SecurityPrincipal principal) {
        return new JwtAuthenticationResponse(this.jwtGenerator.generateJwt(
                principal.getUsername(), principal.getName(), principal.getEmail()));
    }

    /**
     * Revokes currently used token.
     */
    @PostMapping(path = AuthRestPaths.TOKEN_REVOKE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeToken() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        this.revokedTokenService.revoke(jwtAuthenticationToken.getJwtString());
    }
}
