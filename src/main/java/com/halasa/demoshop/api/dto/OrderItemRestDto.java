package com.halasa.demoshop.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItemRestDto {

    @NotNull
    @Min(0)
    private Integer count;

    @NotNull
    private ProductRestDto product;

    public OrderItemRestDto() {
    }

    public OrderItemRestDto(ProductRestDto product, Integer count) {
        this.count = count;
        this.product = product;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ProductRestDto getProduct() {
        return product;
    }

    public void setProduct(ProductRestDto product) {
        this.product = product;
    }
}
