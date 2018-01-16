package com.halasa.demoshop.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderRestDto extends BasicRestDto {

    private BigDecimal priceWithoutVat;

    private BigDecimal priceWithVat;

    private CustomerRestDto customer;

    private List<OrderItemRestDto> orderItems;

    public OrderRestDto() {
    }

    public OrderRestDto(BigDecimal priceWithoutVat, BigDecimal priceWithVat, CustomerRestDto customer, List<OrderItemRestDto> orderItems) {
        this.priceWithoutVat = priceWithoutVat;
        this.priceWithVat = priceWithVat;
        this.customer = customer;
        this.orderItems = orderItems;
    }

    public BigDecimal getPriceWithoutVat() {
        return priceWithoutVat;
    }

    public void setPriceWithoutVat(BigDecimal priceWithoutVat) {
        this.priceWithoutVat = priceWithoutVat;
    }

    public BigDecimal getPriceWithVat() {
        return priceWithVat;
    }

    public void setPriceWithVat(BigDecimal priceWithVat) {
        this.priceWithVat = priceWithVat;
    }

    public CustomerRestDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerRestDto customer) {
        this.customer = customer;
    }

    public List<OrderItemRestDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRestDto> orderItems) {
        this.orderItems = orderItems;
    }
}
