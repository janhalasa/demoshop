package com.halasa.demoshop.service.domain;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "PICTURE")
@Proxy(lazy = false)
public class Picture extends BasicEntity {

    @Column(name = "NAME")
    @Field
    private String name;

    @Column(name = "WIDTH")
    private Integer width;

    @Column(name = "HEIGHT")
    private Integer height;

    @Column(name = "REFERENCE_CODE")
    private String referenceCode;

    private byte[] content;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "picture")
    private Set<Product> products;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "picture")
    private Set<Customer> customers;

    public Picture() {
    }

    public Picture(String name, Integer width, Integer height, String referenceCode, byte[] content, String contentType) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.referenceCode = referenceCode;
        this.content = content;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }
}
