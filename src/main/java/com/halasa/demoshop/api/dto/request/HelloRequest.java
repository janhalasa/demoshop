package com.halasa.demoshop.api.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Optional;

public class HelloRequest {

    @NotNull
    @Size(min = 3, max = 32, message = "This is a message")
    private String name;

    private Optional<ZonedDateTime> dateTime = Optional.empty();

    public HelloRequest() {
    }

    public HelloRequest(String name) {
        this.name = name;
    }

    public HelloRequest(String name, ZonedDateTime dateTime) {
        this.name = name;
        this.dateTime = Optional.of(dateTime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<ZonedDateTime> getDateTime() {
        return dateTime;
    }

    public void setDateTime(Optional<ZonedDateTime> dateTime) {
        this.dateTime = dateTime;
    }
}
