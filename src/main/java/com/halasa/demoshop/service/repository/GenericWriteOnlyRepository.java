package com.halasa.demoshop.service.repository;

public interface GenericWriteOnlyRepository {

    <T> void persist(T entity);

    <T> T merge(T entity);

    <T> T save(T entity);

    <T> void remove(T entity);

    <T, K> void removeByPk(Class<T> entityClass, K pk);

    <T> void removeAll(Class<T> entityClass);
}
