package com.arch.ms_payment.service;


import com.arch.ms_payment.dto.PaymentRequestDto;
import com.arch.ms_payment.dto.PaymentResponseDto;
import com.arch.ms_payment.model.Payment;
import com.arch.ms_payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing payment request: {}", paymentRequest.getPaymentId());

        try {
            // Check if payment already exists
            if (paymentRepository.existsByPaymentId(paymentRequest.getPaymentId())) {
                throw new IllegalArgumentException("Payment with ID " + paymentRequest.getPaymentId() + " already exists");
            }

            // Create and save payment entity
            Payment payment = Payment.builder()
                    .paymentId(paymentRequest.getPaymentId())
                    .userId(paymentRequest.getUserId())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .description(paymentRequest.getDescription())
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .status(Payment.PaymentStatus.PROCESSED)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved successfully: {}", savedPayment.getPaymentId());

            return PaymentResponseDto.builder()
                    .paymentId(savedPayment.getPaymentId())
                    .userId(savedPayment.getUserId())
                    .amount(savedPayment.getAmount())
                    .currency(savedPayment.getCurrency())
                    .description(savedPayment.getDescription())
                    .status(savedPayment.getStatus().name())
                    .paymentMethod(savedPayment.getPaymentMethod())
                    .processedAt(savedPayment.getCreatedAt())
                    .success(true)
                    .message("Payment processed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error processing payment {}: {}", paymentRequest.getPaymentId(), e.getMessage());

            return PaymentResponseDto.builder()
                    .paymentId(paymentRequest.getPaymentId())
                    .userId(paymentRequest.getUserId())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .processedAt(LocalDateTime.now())
                    .success(false)
                    .message("Payment processing failed: " + e.getMessage())
                    .errorCode("PROCESSING_ERROR")
                    .build();
        }
    }
}
