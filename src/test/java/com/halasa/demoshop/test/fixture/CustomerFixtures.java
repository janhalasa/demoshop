package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Picture;
import org.apache.commons.lang3.RandomUtils;

public class CustomerFixtures {

    public static Customer some() {
        return some(null);
    }

    public static Customer some(Picture picture) {
        long random = RandomUtils.nextInt();
        return new Customer(
                "firstName" + random,
                "lastName" + random,
                "telephone" + random,
                "email" + random,
                picture
        );
    }
}
