package com.arch.ms_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private String paymentId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String status;
    private String paymentMethod;
    private LocalDateTime processedAt;
    private boolean success;
    private String message;
    private String errorCode;
}