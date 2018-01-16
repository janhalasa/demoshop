package com.halasa.demoshop.rest;

import com.halasa.demoshop.service.OrderBy;
import com.halasa.demoshop.service.OrderByDirection;
import com.halasa.demoshop.service.validation.ValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrderByParser {

    public static final Pattern SINGLE = Pattern.compile("^[a-zA-Z0-9.]+([ ]*:[ ]*([Aa][Ss][Cc]|[Dd][Ee][Ss][Cc]))?$");

    public List<OrderBy> parse(String orderByString) {
        if (orderByString == null || orderByString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = orderByString.split(",");
        return Arrays.stream(parts)
                .map(part -> this.parseOne(part))
                .collect(Collectors.toList());
    }

    OrderBy parseOne(String fieldAndDirection) {
        if (! SINGLE.matcher(fieldAndDirection.trim()).matches()) {
            throw new ValidationException(
                    ErrorCode.ORDER_BY_INVALID_FORMAT,
                    "Order by value '" + fieldAndDirection + "' has invalid syntax.");
        }
        String[] parts = fieldAndDirection.split(":");
        if (parts.length == 1) {
            return new OrderBy(parts[0].trim());
        }
        return new OrderBy(parts[0].trim(), OrderByDirection.valueOf(parts[1].trim().toUpperCase()));
    }
}
