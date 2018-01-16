package com.halasa.demoshop.rest.mapper;

import com.halasa.demoshop.api.dto.OrderRestDto;
import com.halasa.demoshop.service.domain.Order;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withCustom = { CustomerRestMapper.class, OrderItemRestMapper.class })
public interface OrderRestMapper {

    OrderRestDto asOrderRestDto(Order in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Order asOrder(OrderRestDto in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Order asOrder(OrderRestDto in, Order out);
}
