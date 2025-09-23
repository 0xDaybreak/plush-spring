package com.codingday.plushspring.model;

import java.math.BigDecimal;

public class BuyRequest {

    private Long id;
    private BigDecimal quantity;


    public BuyRequest(Long id, BigDecimal quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
