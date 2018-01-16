package com.halasa.demoshop.service.domain;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REVOKED_TOKEN")
@Proxy(lazy = false)
public class RevokedToken {

    @Column(name = "JWT_HASH")
    @Id
    private String jwtHash;

    public RevokedToken() {
    }

    public RevokedToken(String jwtHash) {
        this.jwtHash = jwtHash;
    }

    public String getJwtHash() {
        return jwtHash;
    }

    public void setJwtHash(String jwtHash) {
        this.jwtHash = jwtHash;
    }
}
