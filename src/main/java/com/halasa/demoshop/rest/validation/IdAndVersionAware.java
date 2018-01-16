package com.halasa.demoshop.rest.validation;

public interface IdAndVersionAware<T> {

    T getId();

    Long getEntityVersion();
}
