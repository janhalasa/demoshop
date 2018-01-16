package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.Customer_;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Picture_;
import com.halasa.demoshop.service.domain.Product_;
import com.halasa.demoshop.service.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.Optional;

@Repository
public class PictureRepositoryJpa extends BasicReadOnlyRepositoryJpa<Picture, Long> implements PictureRepository {

    @Autowired
    public PictureRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(PictureRepositoryJpa.class, Picture.class, Collections.EMPTY_LIST, entityManager, entityManagerFactory);
    }

    public Optional<Picture> getByReferenceCode(String referenceCode) {
        return this.getWhere((criteriaBuilder, root) -> criteriaBuilder.equal(root.get(Picture_.referenceCode), referenceCode));
    }

    public Picture loadByProduct(Long productId) {
        return this.loadWhere((criteriaBuilder, root) -> criteriaBuilder
                .equal(root.join(Picture_.products).get(Product_.id), productId));
    }

    public Picture loadByCustomer(Long customerId) {
        return this.loadWhere((criteriaBuilder, root) -> criteriaBuilder
                .equal(root.join(Picture_.customers).get(Customer_.id), customerId));
    }
}
