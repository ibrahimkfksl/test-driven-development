package com.ibrahim.tdd.services.requests;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    private String productCode;

    private Integer amount;

    private BigDecimal unitPrice;
}
