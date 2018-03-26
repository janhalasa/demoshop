package com.halasa.demoshop.app.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.halasa.demoshop.api.CustomerRestPaths;
import com.halasa.demoshop.api.dto.CustomerRestDto;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.mapper.CustomerRestMapper;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.PictureService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.repository.CustomerRepository;
import com.halasa.demoshop.test.fixture.CustomerFixtures;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerE2eTest extends EndToEndTestBase {

    @Autowired
    private CustomerRestMapper customerRestMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private PictureRestMapper pictureRestMapper;

    @Test
    @WithMockUser
    public void testGet() throws Exception {
        final Customer customer = this.genericWriteOnlyRepository.save(CustomerFixtures.some());

        final MvcResult mvcResult = this.mockMvc.perform(
                get(CustomerRestPaths.GET, customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomerRestDto customerResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerRestDto.class);

        this.assertCustomerOwnFieldsEqual(customer, customerResponse);
        this.assertBasicEntityFieldsEqual(customer, customerResponse);
    }

    @Test
    @WithMockUser
    public void testGetPicture() throws Exception {
        final Picture picture = PictureFixtures.some();
        final Customer customer = this.genericWriteOnlyRepository.save(CustomerFixtures.some(picture));

        final MvcResult mvcResult = this.mockMvc.perform(
                get(CustomerRestPaths.GET_PICTURE, customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PictureRestDto pictureRestDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PictureRestDto.class);

        PictureE2eTest.assertPictureOwnFieldsEqual(picture, pictureRestDto);
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreate() throws Exception {
        final CustomerRestDto customerCreateRequest = this.customerRestMapper.asCustomerRestDto(
                CustomerFixtures.some(PictureFixtures.some()));

        final MvcResult mvcResult = this.mockMvc.perform(
                post(CustomerRestPaths.CREATE)
                        .content(objectMapper.writeValueAsString(customerCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerRestDto customerResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerRestDto.class);

        this.assertCustomerOwnFieldsEqual(customerRestMapper.asCustomer(customerCreateRequest), customerResponse);
        this.assertBasicEntityFieldsCreated(customerCreateRequest, customerResponse);
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreateWithPictureReference() throws Exception {
        final Picture picture = pictureService.save(PictureFixtures.some());

        final CustomerRestDto customerCreateRequest = this.customerRestMapper.asCustomerRestDto(CustomerFixtures.some());

        final URI uri = UriComponentsBuilder.fromPath(CustomerRestPaths.CREATE)
                .queryParam("pictureReferenceCode", picture.getReferenceCode())
//                .queryParam("fetches", "picture")
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc.perform(
                post(uri)
                        .content(objectMapper.writeValueAsString(customerCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerRestDto customerResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerRestDto.class);

        Picture pictureOfCustomer = this.customerRepository.getByPk(customerResponse.getId(), Arrays.asList("picture")).getPicture();

        this.assertCustomerOwnFieldsEqual(customerRestMapper.asCustomer(customerCreateRequest), customerResponse);
        this.assertBasicEntityFieldsCreated(customerCreateRequest, customerResponse);

        PictureE2eTest.assertPictureOwnFieldsEqual(picture, pictureRestMapper.asPictureRestDto(pictureOfCustomer));
    }

    private Customer createCustomer(Customer customer) {
        return genericWriteOnlyRepository.save(customer);
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testUpdate() throws Exception {
        final Customer originalCustomer = createCustomer(CustomerFixtures.some());

        // Sleep to make sure the creation/update times are not the same after update
        Thread.sleep(10);

        final CustomerRestDto customerUpdateRequest = this.customerRestMapper.asCustomerRestDto(CustomerFixtures.some());
        customerUpdateRequest.setId(originalCustomer.getId());
        customerUpdateRequest.setEntityVersion(originalCustomer.getEntityVersion());

        final MvcResult mvcResult = this.mockMvc.perform(
                put(CustomerRestPaths.UPDATE, originalCustomer.getId())
                        .content(objectMapper.writeValueAsString(customerUpdateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CustomerRestDto customerResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerRestDto.class);

        this.assertCustomerOwnFieldsEqual(this.customerRestMapper.asCustomer(customerUpdateRequest), customerResponse);

        Assert.assertEquals(customerUpdateRequest.getId(), customerResponse.getId());
        Assert.assertEquals(originalCustomer.getCreatedAt(), originalCustomer.getUpdatedAt());
        Assert.assertTrue(originalCustomer.getUpdatedAt() + " not before " + customerResponse.getUpdatedAt(),
                originalCustomer.getUpdatedAt().isBefore(customerResponse.getUpdatedAt()));
        Assert.assertEquals(new Long(customerUpdateRequest.getEntityVersion() + 1L), customerResponse.getEntityVersion());
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testDelete() throws Exception {
        Customer customer = createCustomer(CustomerFixtures.some());

        this.mockMvc.perform(
                delete(CustomerRestPaths.DELETE, customer.getId()))
                .andExpect(status().isNoContent());
    }

    private void assertCustomerOwnFieldsEqual(Customer expected, CustomerRestDto actual) {
        Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(expected.getLastName(), actual.getLastName());
        Assert.assertEquals(expected.getTelephone(), actual.getTelephone());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    @WithMockUser
    public void testSearchByNamePart() throws Exception {
        final Customer customer = this.createCustomer(CustomerFixtures.some());

        // Create some other customers, just to have more of them
        this.createCustomer(CustomerFixtures.some());
        this.createCustomer(CustomerFixtures.some());
        this.createCustomer(CustomerFixtures.some());
        this.createCustomer(CustomerFixtures.some());

        final URI uri = UriComponentsBuilder.fromPath(CustomerRestPaths.SEARCH)
                .queryParam("filter", String.format("firstName==*%s*;lastName==*%s*",
                        customer.getFirstName().substring(1, customer.getFirstName().length() - 1),
                        customer.getLastName().substring(1, customer.getLastName().length() - 1)))
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc
                .perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<CustomerRestDto> customerResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ListResponse<CustomerRestDto>>() {});

        Assert.assertEquals(1, customerResponse.getResults().size());
        Assert.assertEquals(customer.getId(), customerResponse.getResults().get(0).getId());
        Assert.assertEquals(customer.getFirstName(), customerResponse.getResults().get(0).getFirstName());
        Assert.assertEquals(customer.getLastName(), customerResponse.getResults().get(0).getLastName());
    }
}
