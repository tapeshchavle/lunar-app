package com.lunar.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    
    private Long id;
    private String ticketCode;
    private String qrCode;
    private String qrCodeImageUrl;
    private String status;
    private String seatNumber;
    private String section;
    private String rowNumber;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Long transferToUserId;
    private LocalDateTime transferredAt;
    private String transferNotes;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private String usedBy;
    private Long bookingId;
    private Long ticketTypeId;
    private String ticketTypeName;
    private Long userId;
    private String userName;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
