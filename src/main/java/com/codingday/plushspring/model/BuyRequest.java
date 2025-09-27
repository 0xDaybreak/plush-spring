package com.codingday.plushspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class BuyRequest {

    private Long customerId;
    private List<String> plushIds;

    private String payload;


    public BuyRequest() {

    }

    public BuyRequest(Long customerId, List<String> plushIds, String payload) {
        this.customerId = customerId;
        this.plushIds = plushIds;
        this.payload = payload;
    }
}
