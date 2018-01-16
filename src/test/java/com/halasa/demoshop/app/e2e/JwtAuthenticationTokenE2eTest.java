package com.halasa.demoshop.app.e2e;

import com.halasa.demoshop.api.AuthRestPaths;
import com.halasa.demoshop.api.ProductRestPaths;
import com.halasa.demoshop.api.dto.response.JwtAuthenticationResponse;
import com.halasa.demoshop.app.security.JwtGenerator;
import com.halasa.demoshop.service.RevokedTokenService;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;

import static com.halasa.demoshop.app.security.UserDetailsServiceMock.ADMIN;
import static com.halasa.demoshop.app.security.UserDetailsServiceMock.CUSTOMER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtAuthenticationTokenE2eTest extends EndToEndTestBase {

    private final String validBasicAuth;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private RevokedTokenService revokedTokenService;

    public JwtAuthenticationTokenE2eTest() {
        this.validBasicAuth = "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());
    }

    @Test
    public void testEndpointSecurityIsRequired() throws Exception {
        this.mockMvc.perform(
                post(AuthRestPaths.TOKEN)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("some:fake".getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testValidCredentialsAreAccepted() throws Exception {
        final MvcResult mvcResult = this.mockMvc.perform(
                post(AuthRestPaths.TOKEN)
                        .header(HttpHeaders.AUTHORIZATION, this.validBasicAuth)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JwtAuthenticationResponse jwtResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                JwtAuthenticationResponse.class);

        Assert.assertNotNull(jwtResponse.getToken());
    }

    @Test
    public void testLocalJwtIsAcceptedInApi() throws Exception {
        final String token = this.jwtGenerator.generateJwt("admin", "Kermit the Frog", "mit@kerm.it");

        // Perform a simple product search just to check we don't get the HTTP 401 error
        this.mockMvc
                .perform(get(ProductRestPaths.SEARCH)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testApiRequiresToken() throws Exception {
        this.mockMvc
                .perform(get(ProductRestPaths.SEARCH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testInvalidJwtIsRejectedInApi() throws Exception {
        final String token = "bug" + this.jwtGenerator.generateJwt("admin", "Kermit the Frog", "mit@kerm.it");

        // Perform a simple product search just to check we don't get the HTTP 401 error
        this.mockMvc
                .perform(get(ProductRestPaths.SEARCH)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithUserDetails(CUSTOMER)
    public void testTokenRenew() throws Exception {
        final MvcResult mvcResult = this.mockMvc
                .perform(post(AuthRestPaths.TOKEN_RENEW)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JwtAuthenticationResponse jwtResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                JwtAuthenticationResponse.class);

        Assert.assertNotNull(jwtResponse.getToken());
        final JWT jwt = JWTParser.parse(jwtResponse.getToken());
        Assert.assertEquals("customer@ved.ro", jwt.getJWTClaimsSet().getSubject());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testTokenRevoke() throws Exception {
        final String token = this.jwtGenerator.generateJwt("admin", "Kermit the Frog", "mit@kerm.it");

        this.mockMvc
                .perform(post(AuthRestPaths.TOKEN_REVOKE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent())
                .andReturn();

        Assert.assertTrue(this.revokedTokenService.isRevoked(token));

        this.mockMvc
                .perform(post(AuthRestPaths.TOKEN_REVOKE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
