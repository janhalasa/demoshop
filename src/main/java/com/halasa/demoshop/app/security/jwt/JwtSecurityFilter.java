package com.halasa.demoshop.app.security.jwt;

import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.halasa.demoshop.service.RevokedTokenService;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public class JwtSecurityFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecurityFilter.class);

    private final List<JwtVerifier> jwtVerifiers;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthSuccessHandler authSuccessHandler;
    private final RevokedTokenService revokedTokenService;

    public JwtSecurityFilter(
            List<JwtVerifier> jwtVerifiers,
            AuthenticationEntryPoint authenticationEntryPoint,
            JwtAuthSuccessHandler authSuccessHandler,
            RevokedTokenService revokedTokenService) {
        this.jwtVerifiers = jwtVerifiers;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authSuccessHandler = authSuccessHandler;
        this.revokedTokenService = revokedTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {

        Optional<String> optionalJwtString = this.resolveToken(request);

        if (optionalJwtString.isPresent()) {
            final String jwtString = optionalJwtString.get();
            try {
                try {
                    final JWT jwt = JWTParser.parse(jwtString);
                    final JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
                    final String issuer = claimSet.getIssuer();
                    final JwtVerifier jwtVerifier = this.jwtManagerByIssuer(issuer);
                    jwtVerifier.validate(jwt, jwtString, claimSet);

                    JwtAuthenticationToken authenticationToken = jwtVerifier.getAuthentication(jwt, jwtString, claimSet);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    authSuccessHandler.handle(authenticationToken);

                    if (this.revokedTokenService.isRevoked(jwtString)) {
                        throw new BadCredentialsException("Revoked JWT: " + jwtString);
                    }
                } catch (ParseException ex) {
                    throw new BadCredentialsException("Error parsing JWT " + jwtString, ex);
                }
            } catch (AuthenticationException ex) {
                LOGGER.warn("JWT Authentication failed " + jwtString, ex);
                SecurityContextHolder.clearContext();
                this.authenticationEntryPoint.commence(request, response, ex);

                return;
            }
        }

        filterChain.doFilter(request, response);

        this.resetAuthenticationAfterRequest();
    }

    private JwtVerifier jwtManagerByIssuer(String issuer) {
        return jwtVerifiers.stream()
                .filter(jwtVerifier -> jwtVerifier.acceptsIssuer(issuer))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Unsupported JWT issuer: " + issuer));
    }

    private void resetAuthenticationAfterRequest() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Optional.ofNullable(bearerToken.substring(7, bearerToken.length()));
        }
        return Optional.empty();
    }

}
