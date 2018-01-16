package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.BasicEntity;
import org.apache.commons.lang3.RandomUtils;

import java.time.ZonedDateTime;

public class BasicEntityFixtures {

    public static <T extends BasicEntity> T setBasicFields(T entity) {
        entity.setId((long) RandomUtils.nextInt() % 1000);
        entity.setEntityVersion((long) RandomUtils.nextInt() % 50);
        entity.setCreatedAt(ZonedDateTime.now().minusMinutes(RandomUtils.nextInt() % 1000));
        entity.setUpdatedAt(ZonedDateTime.now().minusSeconds(RandomUtils.nextInt() % 1000));
        return entity;
    }
}
