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
public class BookingResponse {
    
    private Long id;
    private String bookingReference;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal serviceFee;
    private BigDecimal netAmount;
    private String currency;
    private String bookingNotes;
    private String specialRequirements;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private BigDecimal refundAmount;
    private LocalDateTime refundProcessedAt;
    private Long userId;
    private String userName;
    private Long eventId;
    private String eventTitle;
    private Integer totalTickets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
