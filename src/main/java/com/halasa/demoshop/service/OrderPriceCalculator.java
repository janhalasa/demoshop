package com.halasa.demoshop.service;

import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Calculates prices of Orders. It can possibly factor in discounts or some extra expenses.
 */
@Service
public class OrderPriceCalculator {

    public Price calculate(Order order) {
        BigDecimal resultWithoutVat = BigDecimal.ZERO;
        BigDecimal resultWithVat = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            resultWithoutVat = resultWithoutVat.add(orderItem.getProduct().getPriceWithoutVat());
            resultWithVat = resultWithVat.add(orderItem.getProduct().getPriceWithVat());
        }
        return new Price(resultWithoutVat, resultWithVat);
    }

    public static class Price {

        private BigDecimal withVat;
        private BigDecimal withoutVat;

        public Price(BigDecimal withoutVat, BigDecimal withVat) {
            this.withVat = withVat;
            this.withoutVat = withoutVat;
        }

        public BigDecimal getWithVat() {
            return withVat;
        }

        public BigDecimal getWithoutVat() {
            return withoutVat;
        }
    }
}
