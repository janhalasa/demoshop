package com.halasa.demoshop.rest.mapper;

import com.halasa.demoshop.api.dto.ProductRestDto;
import com.halasa.demoshop.service.domain.Product;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withCustom = PictureRestMapper.class)
public interface ProductRestMapper {

    ProductRestDto asProductRestDto(Product in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Product asProduct(ProductRestDto in);

    @Maps(withIgnoreFields = { "id", "createdAt", "updatedAt" })
    Product asProduct(ProductRestDto in, Product out);
}
