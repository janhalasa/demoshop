package com.halasa.demoshop.app.doc;

import com.halasa.demoshop.api.CustomerRestPaths;
import com.halasa.demoshop.api.dto.CustomerRestDto;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.controller.CustomerController;
import com.halasa.demoshop.rest.mapper.CustomerRestMapper;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.test.fixture.BasicEntityFixtures;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;

import static com.halasa.demoshop.app.security.UserDetailsServiceMock.ADMIN;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerDocumentation extends ApiDocumentationBase {

    @Autowired
    private CustomerRestMapper customerRestMapper;

    @MockBean
    private PictureRepository pictureRepository;

    private Customer customer;

    private ListResult<Customer> customerList;

    @Before
    public void beforeEachTest() {
        Picture picture = BasicEntityFixtures.setBasicFields(PictureFixtures.some());

        this.customer = BasicEntityFixtures.setBasicFields(new Customer(
                "Jára", "Cimrman", "+420123456789", "jarda@cimrman.cz", picture
        ));

        customerList = new ListResult<Customer>(
                Arrays.asList(this.customer),
                Optional.of(57L),
                Optional.of(3),
                Optional.of(10)
        );

        when(customerRepository.getByPk(Matchers.anyLong())).thenReturn(customer);
        when(customerRepository.getByPk(Matchers.anyLong(), any())).thenReturn(customer);
        when(customerRepository.search(any(), any(), any(), any(), any())).thenReturn(customerList);
        when(pictureRepository.loadByCustomer(customer.getId())).thenReturn(picture);
        when(customerService.save(any(), any())).thenReturn(customer);
    }

    @Test
    @WithMockUser
    public void getById() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(CustomerRestPaths.GET)
                .queryParam("fetch", "picture")
                .build().toString();

        this.mockMvc.perform(get(uri, this.customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "customer-get",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Customer identifier")
                        ),
                        requestParameters(
                                parameterWithName("fetch").description("Comma separated list of associations to fetch")
                        ),
                        responseFields(savedCustomerFields(true).build())
                ));
    }

    @Test
    @WithMockUser
    public void getCustomerPicture() throws Exception {
        this.mockMvc.perform(get(CustomerRestPaths.GET_PICTURE, this.customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "customer-picture-get",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Customer identifier")
                        ),
                        responseFields(PictureDocumentation.savedPictureFields().build())
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createCustomer() throws Exception {
        final CustomerRestDto request = this.customerRestMapper.asCustomerRestDto(this.customer);

        this.mockMvc.perform(
                post(CustomerRestPaths.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document(
                        "customer-create",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(
                                parameterWithName("pictureReferenceCode")
                                        .optional()
                                        .description("Reference code of a picture to associate with the customer")
                        ),
                        requestFields(unsavedCustomerFields().build()),
                        responseFields(savedCustomerFields(true).build())
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updateCustomer() throws Exception {
        final CustomerRestDto request = this.customerRestMapper.asCustomerRestDto(this.customer);

        this.mockMvc.perform(
                put(CustomerRestPaths.UPDATE, request.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document(
                        "customer-update",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of a customer to be updated")
                        ),
                        requestParameters(
                                parameterWithName("pictureReferenceCode")
                                        .optional()
                                        .description("Reference code of a picture to associate with the customer")
                        ),
                        requestFields(savedCustomerFields(true).build()),
                        responseFields(savedCustomerFields(true).build())
                ));
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void deleteCustomer() throws Exception {
        this.mockMvc.perform(delete(CustomerRestPaths.DELETE, this.customer.getId()))
                .andExpect(status().isNoContent())
                .andDo(document(
                        "customer-delete",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of a customer to delete")
                        )));
    }

    @Test
    @WithMockUser
    public void search() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(CustomerRestPaths.SEARCH)
                .queryParam("filter", "lastName==*mrman")
                .queryParam("offset", "")
                .queryParam("limit", "")
                .queryParam("fetch", "picture")
                .queryParam("orderBy", "")
                .build().toString();

        this.mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "customer-search",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(searchQueryParams()),
                        responseFields(listResposeFields()
                                .addAll(savedCustomerFields(true).withPrefix("results[].").build())
                                .build())
                ));
    }

    public static FieldDescriptorListBuilder unsavedCustomerFields() {
        return FieldDescriptorListBuilder.from(ApiDocumentationBase.unsavedBasicEntityFields())
                .addAll(customerFields().build())
                .addAll(PictureDocumentation.unsavedPictureFields().withPrefix("picture.").build());
    }

    public static FieldDescriptorListBuilder savedCustomerFields(boolean withPicture) {
        final FieldDescriptorListBuilder builder = FieldDescriptorListBuilder.from(ApiDocumentationBase.basicEntityFields())
                .addAll(customerFields().build());
        if (withPicture) {
            builder.addAll(PictureDocumentation.savedPictureFields().withPrefix("picture.").build());
        }
        return builder;
    }

    private static FieldDescriptorListBuilder customerFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("firstName").description("Customer's first name"),
                fieldWithPath("lastName").description("Customer's last name"),
                fieldWithPath("telephone").description("Customer's telephone number"),
                fieldWithPath("email").description("Customer's email address"),
                fieldWithPath("picture").description("Customer's picture").type(PictureRestDto.class));
    }
}
