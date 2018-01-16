package com.halasa.demoshop.app.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.halasa.demoshop.api.OrderRestPaths;
import com.halasa.demoshop.api.dto.OrderItemRestDto;
import com.halasa.demoshop.api.dto.OrderRestDto;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.rest.mapper.OrderItemRestMapper;
import com.halasa.demoshop.rest.mapper.OrderRestMapper;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.OrderPriceCalculator;
import com.halasa.demoshop.service.OrderService;
import com.halasa.demoshop.service.PictureService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import com.halasa.demoshop.service.repository.OrderRepository;
import com.halasa.demoshop.test.fixture.CustomerFixtures;
import com.halasa.demoshop.test.fixture.OrderFixtures;
import com.halasa.demoshop.test.fixture.OrderItemFixtures;
import com.halasa.demoshop.test.fixture.ProductFixtures;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.halasa.demoshop.app.security.UserDetailsServiceMock.ADMIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderE2eTest extends EndToEndTestBase {

    @Autowired
    private OrderRestMapper orderRestMapper;

    @Autowired
    private OrderItemRestMapper orderItemRestMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private PictureRestMapper pictureRestMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPriceCalculator orderPriceCalculator;

    @Test
    @WithUserDetails(ADMIN)
    public void testGet() throws Exception {
        final Order order = this.createAndSaveSomeOrder();

        final MvcResult mvcResult = this.mockMvc.perform(
                get(OrderRestPaths.GET, order.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderRestDto orderResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderRestDto.class);

        this.assertOrderOwnFieldsEqual(order, orderResponse);
        this.assertBasicEntityFieldsEqual(order, orderResponse);
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testCreate() throws Exception {
        final Customer customer = this.genericWriteOnlyRepository.save(CustomerFixtures.some());
        final List<OrderItem> orderItems = Arrays.asList(
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some())),
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some())),
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some()))
        );

        final Order order = OrderFixtures.some(customer, orderItems);
        final OrderPriceCalculator.Price price = orderPriceCalculator.calculate(order);
        order.setPriceWithoutVat(price.getWithoutVat());
        order.setPriceWithVat(price.getWithVat());
        final OrderRestDto orderCreateRequest = this.orderRestMapper.asOrderRestDto(order);

        final MvcResult mvcResult = this.mockMvc.perform(
                post(OrderRestPaths.CREATE)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        OrderRestDto orderResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderRestDto.class);

        this.assertOrderOwnFieldsEqual(orderRestMapper.asOrder(orderCreateRequest), orderResponse);
        this.assertBasicEntityFieldsCreated(orderCreateRequest, orderResponse);
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testUpdate() throws Exception {
        final Order originalOrder = this.createAndSaveSomeOrder();

        final OrderRestDto orderUpdateRequest = this.orderRestMapper.asOrderRestDto(originalOrder);
        orderUpdateRequest.getOrderItems().forEach(orderItemRestDto -> orderItemRestDto.setCount(orderItemRestDto.getCount() + 1));
        for (int i = 0; i < 2; i++) {
            orderUpdateRequest.getOrderItems().add(this.orderItemRestMapper.asOrderItemRestDto(
                    OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some()))));
        }
        orderUpdateRequest.getOrderItems().remove(0);

        final OrderPriceCalculator.Price price = orderPriceCalculator.calculate(this.orderRestMapper.asOrder(orderUpdateRequest));
        orderUpdateRequest.setPriceWithoutVat(price.getWithoutVat());
        orderUpdateRequest.setPriceWithVat(price.getWithVat());

        final MvcResult mvcResult = this.mockMvc.perform(
                put(OrderRestPaths.UPDATE, originalOrder.getId())
                        .content(objectMapper.writeValueAsString(orderUpdateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderRestDto orderResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderRestDto.class);

        this.assertOrderOwnFieldsEqual(this.orderRestMapper.asOrder(orderUpdateRequest), orderResponse);

        Assert.assertEquals(orderUpdateRequest.getId(), orderResponse.getId());
        Assert.assertTrue(originalOrder.getUpdatedAt().isBefore(orderResponse.getUpdatedAt()));
        Assert.assertEquals(new Long(orderUpdateRequest.getEntityVersion() + 1L), orderResponse.getEntityVersion());
    }

    private Order createAndSaveSomeOrder() {
        final Customer customer = this.genericWriteOnlyRepository.save(CustomerFixtures.some());
        final List<OrderItem> orderItems = Arrays.asList(
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some())),
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some())),
                OrderItemFixtures.some(this.genericWriteOnlyRepository.save(ProductFixtures.some()))
        );
        return this.orderService.save(OrderFixtures.some(customer, orderItems));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    @WithUserDetails(ADMIN)
    public void testDelete() throws Exception {
        final Order order = this.createAndSaveSomeOrder();

        this.mockMvc.perform(
                delete(OrderRestPaths.DELETE, order.getId()))
                .andExpect(status().isNoContent());

        this.orderRepository.getByPk(order.getId());
    }

    private void assertOrderOwnFieldsEqual(Order expected, OrderRestDto actual) {
        Assert.assertEquals(expected.getPriceWithoutVat() + " != " + actual.getPriceWithoutVat(),
                BigDecimal.ZERO, expected.getPriceWithoutVat().subtract(actual.getPriceWithoutVat()).stripTrailingZeros());
        Assert.assertEquals(expected.getPriceWithVat() + " != " + actual.getPriceWithVat(),
                BigDecimal.ZERO, expected.getPriceWithVat().subtract(actual.getPriceWithVat()).stripTrailingZeros());
        Assert.assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
        Assert.assertEquals(expected.getOrderItems().size(), actual.getOrderItems().size());
        expected.getOrderItems().forEach(expectedOrderItem -> {
            OrderItemRestDto actualOrderItemDto = actual.getOrderItems().stream()
                    .filter(orderItemRestDto -> orderItemRestDto.getProduct().getId().equals(expectedOrderItem.getProduct().getId()))
                    .findFirst()
                    .get();
            Assert.assertEquals(expectedOrderItem.getCount(), actualOrderItemDto.getCount());
        });
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testSearchByPriceWithoutVat() throws Exception {
        final Order order = this.createAndSaveSomeOrder();

        // Create some other orders, just to have more of them
        this.createAndSaveSomeOrder();
        this.createAndSaveSomeOrder();
        this.createAndSaveSomeOrder();
        this.createAndSaveSomeOrder();

        final URI uri = UriComponentsBuilder.fromPath(OrderRestPaths.SEARCH)
                .queryParam("filter", "priceWithoutVat==" + order.getPriceWithoutVat())
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc
                .perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<OrderRestDto> orderResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ListResponse<OrderRestDto>>() {});

        Assert.assertEquals(1, orderResponse.getResults().size());
        Assert.assertEquals(order.getId(), orderResponse.getResults().get(0).getId());
        Assert.assertEquals(order.getPriceWithoutVat() + " != " + orderResponse.getResults().get(0).getPriceWithoutVat(),
                BigDecimal.ZERO,
                order.getPriceWithoutVat().subtract(orderResponse.getResults().get(0).getPriceWithoutVat()).stripTrailingZeros());
    }
}
