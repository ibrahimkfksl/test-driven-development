package com.ibrahim.tdd.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderDto {

    private BigDecimal totalPrice;
    private Integer id;
}
