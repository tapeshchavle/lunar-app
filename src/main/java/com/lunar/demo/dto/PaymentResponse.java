package com.lunar.demo.dto;

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
public class PaymentResponse {
    
    private Long id;
    private String paymentReference;
    private String externalPaymentId;
    private String paymentMethod;
    private String status;
    private BigDecimal amount;
    private String currency;
    private BigDecimal processingFee;
    private BigDecimal netAmount;
    private String paymentGateway;
    private String gatewayTransactionId;
    private String failureReason;
    private LocalDateTime processedAt;
    private LocalDateTime refundedAt;
    private BigDecimal refundAmount;
    private String refundReason;
    private Long bookingId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Razorpay specific fields
    private String razorpayOrderId;
    private String razorpayKeyId;
}
