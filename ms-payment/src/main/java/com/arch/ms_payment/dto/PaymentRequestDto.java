package com.arch.ms_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private String paymentId;

    private String userId;

    private BigDecimal amount;
    private String currency;
    private String description;

    private String paymentMethod;
}
