package com.halasa.demoshop.rest.mapper;

import com.halasa.demoshop.api.dto.CustomerRestDto;
import com.halasa.demoshop.service.domain.Customer;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withCustom = PictureRestMapper.class, withIgnoreFields = "orders")
public interface CustomerRestMapper {

    CustomerRestDto asCustomerRestDto(Customer in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Customer asCustomer(CustomerRestDto in);

    @Maps(withIgnoreFields = { "id", "createdAt", "updatedAt" })
    Customer asCustomer(CustomerRestDto in, Customer out);
}
