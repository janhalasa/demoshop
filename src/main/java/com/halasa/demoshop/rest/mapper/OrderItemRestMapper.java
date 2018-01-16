package com.halasa.demoshop.rest.mapper;

import com.halasa.demoshop.api.dto.OrderItemRestDto;
import com.halasa.demoshop.service.domain.OrderItem;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withCustom = { ProductRestMapper.class }, withIgnoreFields = {"order", "id"})
public interface OrderItemRestMapper {

    OrderItemRestDto asOrderItemRestDto(OrderItem in);

    @Maps()
    OrderItem asOrderItem(OrderItemRestDto in);

    @Maps()
    OrderItem asOrderItem(OrderItemRestDto in, OrderItem out);
}
