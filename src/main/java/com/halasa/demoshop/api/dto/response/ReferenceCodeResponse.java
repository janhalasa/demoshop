package com.halasa.demoshop.api.dto.response;

public class ReferenceCodeResponse {

    private String referenceCode;

    public ReferenceCodeResponse() {
    }

    public ReferenceCodeResponse(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }
}
