package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Product;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;

public class ProductFixtures {

    public static Product some() {
        return some(null);
    }

    public static Product some(Picture picture) {
        long random = RandomUtils.nextInt();
        BigDecimal priceWithoutVat = BigDecimal.valueOf(RandomUtils.nextInt(10, 1000));
        final Product product = new Product(
                "code" + random,
                "name" + random,
                "desc" + random,
                priceWithoutVat,
                priceWithoutVat.multiply(new BigDecimal("1.2")),
                null
        );
        product.setPicture(picture);
        return product;
    }
}
