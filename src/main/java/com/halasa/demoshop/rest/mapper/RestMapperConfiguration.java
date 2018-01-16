package com.halasa.demoshop.rest.mapper;

import fr.xebia.extras.selma.Selma;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestMapperConfiguration {

    @Bean
    public PictureRestMapper pictureRestMapper() {
        return Selma.builder(PictureRestMapper.class).build();
    }

    @Bean
    public ProductRestMapper productRestMapper(PictureRestMapper pictureRestMapper) {
        return Selma.builder(ProductRestMapper.class)
                .withCustom(pictureRestMapper)
                .build();
    }

    @Bean
    public OrderItemRestMapper orderItemRestMapper(ProductRestMapper productRestMapper) {
        return Selma.builder(OrderItemRestMapper.class)
                .withCustom(productRestMapper)
                .build();
    }

    @Bean
    public OrderRestMapper orderRestMapper(CustomerRestMapper customerRestMapper, OrderItemRestMapper orderItemRestMapper) {
        return Selma.builder(OrderRestMapper.class)
                .withCustom(customerRestMapper, orderItemRestMapper)
                .build();
    }

    @Bean
    public CustomerRestMapper customerRestMapper(PictureRestMapper pictureRestMapper) {
        return Selma.builder(CustomerRestMapper.class)
                .withCustom(pictureRestMapper)
                .build();
    }
}
