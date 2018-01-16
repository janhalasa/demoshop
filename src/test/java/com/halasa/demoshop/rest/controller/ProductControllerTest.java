package com.halasa.demoshop.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halasa.demoshop.api.ProductRestPaths;
import com.halasa.demoshop.api.dto.ProductRestDto;
import com.halasa.demoshop.api.dto.response.ErrorResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.rest.mapper.ProductRestMapper;
import com.halasa.demoshop.service.CustomerService;
import com.halasa.demoshop.service.ProductService;
import com.halasa.demoshop.service.RevokedTokenService;
import com.halasa.demoshop.service.repository.CustomerRepository;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.service.repository.ProductRepository;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import com.halasa.demoshop.test.fixture.ProductFixtures;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for REST interface constraints - without lower tiers.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@ComponentScan({
        "com.halasa.demoshop.rest.mapper",
        "com.halasa.demoshop.rest.converter", /** Needed for the StringSanitizerModule */
        "com.halasa.demoshop.app.security"
})
public class ProductControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ProductRestMapper productRestMapper;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private PictureRepository pictureRepository;
    @MockBean
    private ProductService productService;
    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private RevokedTokenService revokedTokenService;

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testProductCodeRequired() throws Exception {
        final ProductRestDto productCreateRequest = this.productRestMapper.asProductRestDto(
                ProductFixtures.some(PictureFixtures.some()));
        productCreateRequest.setCode("");

        final MvcResult mvcResult = this.mockMvc.perform(
                post(ProductRestPaths.CREATE)
                        .content(objectMapper.writeValueAsString(productCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        Assert.assertEquals(ErrorCode.VALIDATION_FAILURE, errorResponse.getCode());
        Assert.assertTrue(errorResponse.getMessage().contains("code"));
    }
}
