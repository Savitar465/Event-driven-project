package com.arch.ms_ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    private Long productId;
    private String product;
    private int quantity;
}
