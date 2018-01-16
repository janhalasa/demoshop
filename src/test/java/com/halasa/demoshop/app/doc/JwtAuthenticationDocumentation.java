package com.halasa.demoshop.app.doc;

import com.halasa.demoshop.api.AuthRestPaths;
import com.halasa.demoshop.app.security.JwtGenerator;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.controller.JwtAuthenticationController;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.test.fixture.BasicEntityFixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Base64;
import java.util.Optional;

import static com.halasa.demoshop.app.security.UserDetailsServiceMock.ADMIN;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JwtAuthenticationController.class)
@ComponentScan("com.halasa.demoshop.app.security")
public class JwtAuthenticationDocumentation extends ApiDocumentationBase {

    private String basicAuthHeaderValue;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Before
    public void beforeEachTest() {
        Customer customer = BasicEntityFixtures.setBasicFields(new Customer(
                "Pedro", "Vedro", "+421123456789", "pedro@ved.ro", null
        ));

        // Warning: real credentials are getting to the documentation. It would be good to extend the basic HTTP auth filter
        // to have injectable authentication data source and mock it here.
        this.basicAuthHeaderValue = "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());

        Mockito.when(customerRepository.getByEmail(Mockito.anyString())).thenReturn(Optional.of(customer));
    }

    @Test
    @WithMockUser(authorities = Roles.TOKEN)
    public void createToken() throws Exception {
        this.mockMvc.perform(
                post(AuthRestPaths.TOKEN)
                        .header(HttpHeaders.AUTHORIZATION, this.basicAuthHeaderValue)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "auth-createToken",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Basic HTTP authentication with username and password")
                        ),
                        responseFields(
                                tokenFieldDescriptor()
                        )
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void renewToken() throws Exception {
        this.mockMvc.perform(
                post(AuthRestPaths.TOKEN_RENEW)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "auth-renewToken",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                tokenFieldDescriptor()
                        )
                ));
    }

    @Test
    public void revokeToken() throws Exception {
        final String token = this.jwtGenerator.generateJwt("pedro@ved.ro", "Kermit the Frog", "pedro@ved.ro");

        this.mockMvc.perform(
                post(AuthRestPaths.TOKEN_REVOKE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent())
                .andDo(document(
                        "auth-revokeToken",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint())
                ));
    }

    private FieldDescriptor tokenFieldDescriptor() {
        return fieldWithPath("token").description("JWT that can be used for authentication for other application endpoints");
    }
}
