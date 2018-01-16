package com.halasa.demoshop.api.dto;

import javax.validation.constraints.NotNull;

public class PictureRestDto extends BasicRestDto {

    @NotNull
    private String name;

    @NotNull
    private Integer width;

    @NotNull
    private Integer height;

    @NotNull
    private String contentType;

    @NotNull
    private byte[] content;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
