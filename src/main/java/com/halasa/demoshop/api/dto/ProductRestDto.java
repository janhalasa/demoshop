package com.halasa.demoshop.api.dto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductRestDto extends BasicRestDto {

    @Size(max = 255)
    @NotNull
    private String code;

    @Size(max = 255)
    @NotNull
    private String name;

    @Size(max = 4000)
    private String description;

    @Min(0)
    @NotNull
    private BigDecimal priceWithoutVat;

    @Min(0)
    @NotNull
    private BigDecimal priceWithVat;

    @Valid
    private PictureRestDto picture;

    public ProductRestDto() {
    }

    public ProductRestDto(String code, String name, String description, BigDecimal priceWithoutVat, BigDecimal priceWithVat) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.priceWithoutVat = priceWithoutVat;
        this.priceWithVat = priceWithVat;
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

    public PictureRestDto getPicture() {
        return picture;
    }

    public void setPicture(PictureRestDto picture) {
        this.picture = picture;
    }
}
