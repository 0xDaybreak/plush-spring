package com.codingday.plushspring.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Entity
@Table(name="plushies")
@Data
@RequiredArgsConstructor
public class Plush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private BigDecimal quantity;

}
