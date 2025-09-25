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


    public BuyRequest(Long customerId, List<String> plushIds) {
        this.customerId = customerId;
        this.plushIds = plushIds;
    }
}
