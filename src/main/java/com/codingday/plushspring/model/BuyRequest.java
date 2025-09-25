package com.codingday.plushspring.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class BuyRequest {

    private Long customerId;
    private List<String> plushIds;


}
