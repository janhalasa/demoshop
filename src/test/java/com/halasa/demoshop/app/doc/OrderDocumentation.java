package com.halasa.demoshop.app.doc;

import com.halasa.demoshop.api.OrderRestPaths;
import com.halasa.demoshop.api.dto.OrderRestDto;
import com.halasa.demoshop.rest.controller.OrderController;
import com.halasa.demoshop.rest.mapper.OrderRestMapper;
import com.halasa.demoshop.service.OrderService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.OrderRepository;
import com.halasa.demoshop.test.fixture.BasicEntityFixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.halasa.demoshop.app.security.UserDetailsServiceMock.ADMIN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

@WebMvcTest(OrderController.class)
public class OrderDocumentation extends ApiDocumentationBase {

    @Autowired
    private OrderRestMapper orderRestMapper;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderService orderService;

    private Order order;

    private ListResult<Order> orderList;

    @Before
    public void beforeEachTest() {
        Customer customer = BasicEntityFixtures.setBasicFields(new Customer(
                "Pedro",
                "Vedro",
                "+421987645123",
                "pedro@ved.ro",
                null
        ));
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(
                        null,
                        BasicEntityFixtures.setBasicFields(new Product(
                                "P0003",
                                "Arolla Film - The Immortal Forest DVD",
                                "Incredible documentary about a natural forest in Tatry",
                                new BigDecimal("7.80"),
                                new BigDecimal("9.90"),
                                null)),
                        10
                ),
                new OrderItem(
                        null,
                        BasicEntityFixtures.setBasicFields(new Product(
                                "P0002",
                                "Sir Joseph Looping II 500",
                                "Warm but lightweight sleeping bag",
                                new BigDecimal("290"),
                                new BigDecimal("319.00"),
                                null)),
                        2
                )
        );

        this.order = BasicEntityFixtures.setBasicFields(new Order(
                customer,
                new BigDecimal("3980.00"),
                new BigDecimal("4320.90"),
                orderItems));

        orderList = new ListResult<Order>(
                Arrays.asList(this.order),
                Optional.of(13L),
                Optional.of(1),
                Optional.of(0)
        );

        when(orderRepository.getByPk(Matchers.anyLong())).thenReturn(this.order);
        when(orderRepository.getByPk(Matchers.anyLong(), any())).thenReturn(this.order);
        when(orderRepository.search(any(), any(), any(), any(), any())).thenReturn(orderList);
        when(customerRepository.getByEmail(anyString())).thenReturn(Optional.of(customer));
        when(orderService.save(any())).thenReturn(this.order);
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getById() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(OrderRestPaths.GET)
                .queryParam("fetch", "customer")
                .build().toString();

        this.mockMvc.perform(get(uri, this.order.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "order-get",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Order identifier")
                        ),
                        requestParameters(
                                parameterWithName("fetch").description("Comma separated list of associations to fetch")
                        ),
                        responseFields(savedOrderFields().build())
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createOrder() throws Exception {
        final OrderRestDto request = this.orderRestMapper.asOrderRestDto(order);

        this.mockMvc.perform(
                post(OrderRestPaths.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document(
                        "order-create",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(unsavedOrderFields().build()),
                        responseFields(savedOrderFields().build())
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updateOrder() throws Exception {
        final OrderRestDto request = this.orderRestMapper.asOrderRestDto(order);

        this.mockMvc.perform(
                put(OrderRestPaths.UPDATE, request.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document(
                        "order-update",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of an order to be updated")
                        ),
                        requestFields(savedOrderFields().build()),
                        responseFields(savedOrderFields().build())
                ));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteOrder() throws Exception {
        this.mockMvc.perform(delete(OrderRestPaths.DELETE, this.order.getId()))
                .andExpect(status().isNoContent())
                .andDo(document(
                        "order-delete",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of an order to delete")
                        )));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void search() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(OrderRestPaths.SEARCH)
                .queryParam("filter", "priceWithoutVat==3980.00")
                .queryParam("offset", "")
                .queryParam("limit", "")
                .queryParam("fetch", "orderItems")
                .queryParam("orderBy", "")
                .build().toString();

        this.mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "order-search",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(searchQueryParams()),
                        responseFields(listResposeFields()
                                .addAll(savedOrderFields().withPrefix("results[].").build())
                                .build())
                ));
    }

    public static FieldDescriptorListBuilder unsavedOrderFields() {
        return FieldDescriptorListBuilder.from(ApiDocumentationBase.unsavedBasicEntityFields())
                .addAll(orderFields().build())
                .addAll(orderItemFields().withPrefix("orderItems[].").build());
    }

    public static FieldDescriptorListBuilder savedOrderFields() {
        return FieldDescriptorListBuilder.from(ApiDocumentationBase.basicEntityFields())
                .addAll(orderFields().build())
                .addAll(orderItemFields().withPrefix("orderItems[].").build());
    }

    private static FieldDescriptorListBuilder orderFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("priceWithoutVat").description("Order price without VAT"),
                fieldWithPath("priceWithVat").description("Order price including VAT"),
                fieldWithPath("customer").description("Customer who made the order")
        ).addAll(CustomerDocumentation.savedCustomerFields(false).withPrefix("customer.").build()).add(
                fieldWithPath("orderItems").description("Items forming this order")
        );
    }

    private static FieldDescriptorListBuilder orderItemFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("count").description("Number of products ordered"),
                fieldWithPath("product").description("Ordered product")
        ).addAll(ProductDocumentation.savedProductFields(false).withPrefix("product.").build());
    }
}
