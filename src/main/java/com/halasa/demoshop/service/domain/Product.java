package com.halasa.demoshop.service.domain;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Indexed
@Table(name = "PRODUCT")
@Proxy(lazy = false)
public class Product extends BasicEntity {

    @Column(name = "CODE")
    @Size(max = 255)
    @Field
    private String code;

    @Column(name = "NAME")
    @Size(max = 255)
    @Field
    private String name;

    @Column(name = "DESCRIPTION")
    @Size(max = 4000)
    @Field
    private String description;

    @Column(name = "PRICE_WITHOUT_VAT")
    private BigDecimal priceWithoutVat;

    @Column(name = "PRICE_WITH_VAT")
    private BigDecimal priceWithVat;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PICTURE_ID")
    private Picture picture;

    public Product() {
    }

    public Product(String code, String name, String description, BigDecimal priceWithoutVat, BigDecimal priceWithVat, Picture picture) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.priceWithoutVat = priceWithoutVat;
        this.priceWithVat = priceWithVat;
        this.picture = picture;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPriceWithoutVat() {
        return priceWithoutVat;
    }

    public void setPriceWithoutVat(BigDecimal priceWithoutVat) {
        this.priceWithoutVat = priceWithoutVat;
    }

    public BigDecimal getPriceWithVat() {
        return priceWithVat;
    }

    public void setPriceWithVat(BigDecimal priceWithVat) {
        this.priceWithVat = priceWithVat;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
