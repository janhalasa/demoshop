package com.halasa.demoshop.service.repository;

import com.halasa.demoshop.service.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends BasicReadOnlyRepository<Product, Long> {

    ListResult<Product> fulltextSearch(String searchTerm, Optional<Integer> limit, Optional<Integer> offset, List<String> fetches);
}
