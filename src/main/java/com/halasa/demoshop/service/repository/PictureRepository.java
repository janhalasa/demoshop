package com.halasa.demoshop.service.repository;

import com.halasa.demoshop.service.domain.Picture;

import java.util.Optional;

public interface PictureRepository extends BasicReadOnlyRepository<Picture, Long> {

    Optional<Picture> getByReferenceCode(String referenceCode);

    Picture loadByProduct(Long productId);

    Picture loadByCustomer(Long customerId);
}
