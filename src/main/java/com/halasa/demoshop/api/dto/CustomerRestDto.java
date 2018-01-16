package com.halasa.demoshop.api.dto;

public class CustomerRestDto extends BasicRestDto {

    private String firstName;
    private String lastName;
    private String telephone;
    private String email;
    private PictureRestDto picture;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PictureRestDto getPicture() {
        return picture;
    }

    public void setPicture(PictureRestDto picture) {
        this.picture = picture;
    }
}
