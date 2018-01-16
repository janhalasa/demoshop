package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.Picture;

import java.util.UUID;

public class PictureFixtures {

    public static Picture some() {
        return new Picture(
                "IMG_1234.jpg",
                6000,
                4000,
                null,
                UUID.randomUUID().toString().getBytes(),
                "image/jpeg"
        );
    }
}
