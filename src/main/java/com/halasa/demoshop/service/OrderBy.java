package com.halasa.demoshop.service;

public class OrderBy {

    private String fieldName;
    private OrderByDirection direction;

    public OrderBy(String fieldName) {
        this(fieldName, OrderByDirection.ASC);
    }

    public OrderBy(String fieldName, OrderByDirection direction) {
        this.fieldName = fieldName;
        this.direction = direction;
    }

    public String getFieldName() {
        return fieldName;
    }

    public OrderByDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderBy orderBy = (OrderBy) o;

        if (fieldName != null ? !fieldName.equals(orderBy.fieldName) : orderBy.fieldName != null) {
            return false;
        }
        return direction == orderBy.direction;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderBy{"
                + "fieldName='" + fieldName + '\''
                + ", direction=" + direction
                + '}';
    }
}
