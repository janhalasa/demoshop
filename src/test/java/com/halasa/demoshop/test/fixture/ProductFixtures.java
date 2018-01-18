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
        return new ProductFixtureBuilder()
                .picture(picture)
                .build();
    }

    public static ProductFixtureBuilder builder() {
        return new ProductFixtureBuilder();
    }

    public static class ProductFixtureBuilder {

        private final Product product;

        public ProductFixtureBuilder() {
            long random = RandomUtils.nextInt();
            BigDecimal priceWithoutVat = BigDecimal.valueOf(RandomUtils.nextInt(10, 1000));

            this.product = new Product(
                    "code" + random,
                    "name" + random,
                    "desc" + random,
                    priceWithoutVat,
                    priceWithoutVat.multiply(new BigDecimal("1.2")),
                    null
            );
        }

        public ProductFixtureBuilder code(String code) {
            this.product.setCode(code);
            return this;
        }

        public ProductFixtureBuilder name(String name) {
            this.product.setName(name);
            return this;
        }

        public ProductFixtureBuilder description(String description) {
            this.product.setDescription(description);
            return this;
        }

        public ProductFixtureBuilder picture(Picture picture) {
            this.product.setPicture(picture);
            return this;
        }

        public Product build() {
            return this.product;
        }
    }
}
