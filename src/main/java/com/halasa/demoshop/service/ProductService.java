package com.halasa.demoshop.service;

import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import com.halasa.demoshop.service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PictureService pictureService;
    private final GenericWriteOnlyRepository genericWriteOnlyRepository;

    @Autowired
    public ProductService(
            ProductRepository productRepository,
            PictureService pictureService,
            GenericWriteOnlyRepository genericWriteOnlyRepository) {
        this.productRepository = productRepository;
        this.genericWriteOnlyRepository = genericWriteOnlyRepository;
        this.pictureService = pictureService;
    }

    @Transactional
    public Product save(Product product, Optional<String> pictureReferenceCode) {
        if (pictureReferenceCode.isPresent()) {
            product.setPicture(this.pictureService.loadByReferenceCode(pictureReferenceCode.get()));
        }
        final Product savedProduct = this.genericWriteOnlyRepository.save(product);
        return this.productRepository.getByPk(savedProduct.getId());
    }

    @Transactional
    public void remove(Long productId) {
        this.genericWriteOnlyRepository.removeByPk(Product.class, productId);
    }
}
