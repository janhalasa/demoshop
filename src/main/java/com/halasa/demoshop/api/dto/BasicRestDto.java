package com.halasa.demoshop.api.dto;

import com.halasa.demoshop.rest.validation.IdAndVersionAware;
import com.halasa.demoshop.rest.validation.IdRequiresVersion;

import java.time.ZonedDateTime;

@IdRequiresVersion
public class BasicRestDto implements IdAndVersionAware<Long> {

    private Long id;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    private Long entityVersion;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }
}
