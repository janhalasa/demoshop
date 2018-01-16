package com.halasa.demoshop.service.repository;

import com.halasa.demoshop.service.OrderBy;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BasicReadOnlyRepository<T, K extends Serializable> {

    T getByPk(K pk);

    T getByPk(K pk, List<String> fetches);

    ListResult<T> search(
            Optional<String> rsqlQuery,
            Optional<Integer> limit,
            Optional<Integer> offset,
            List<String> fetches,
            List<OrderBy> orderByList);
}
