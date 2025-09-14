package com.lunar.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateRequest {
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
    
    @Valid
    @NotNull(message = "Tickets are required")
    @Size(min = 1, message = "At least one ticket must be selected")
    private List<TicketRequest> tickets;
    
    @Size(max = 500, message = "Booking notes must not exceed 500 characters")
    private String bookingNotes;
    
    @Size(max = 500, message = "Special requirements must not exceed 500 characters")
    private String specialRequirements;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketRequest {
        
        @NotNull(message = "Ticket type ID is required")
        private Long ticketTypeId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        @Size(max = 200, message = "Special instructions must not exceed 200 characters")
        private String specialInstructions;
    }
}
