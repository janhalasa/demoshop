package com.halasa.demoshop.service.domain;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ORDER_ITEM")
@Proxy(lazy = false)
public class OrderItem {

    @EmbeddedId
    private OrderItemId id = new OrderItemId();

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    @MapsId("orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    @MapsId("productId")
    private Product product;

    @Column(name = "ITEM_COUNT")
    private Integer count;

    public OrderItem() {
    }

    public OrderItem(Order order, Product product, Integer count) {
        this.order = order;
        this.product = product;
        this.count = count;
    }

    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Embeddable
    public static class OrderItemId implements Serializable {

        @Column(name = "ORDER_ID")
        private Long orderId;

        @Column(name = "PRODUCT_ID")
        private Long productId;

        private OrderItemId() {}

        public OrderItemId(Long orderId, Long productId) {
            this.orderId = orderId;
            this.productId = productId;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrderItemId that = (OrderItemId) o;

            if (!orderId.equals(that.orderId)) {
                return false;
            }
            return productId.equals(that.productId);
        }

        @Override
        public int hashCode() {
            int result = orderId.hashCode();
            result = 31 * result + productId.hashCode();
            return result;
        }
    }

}
