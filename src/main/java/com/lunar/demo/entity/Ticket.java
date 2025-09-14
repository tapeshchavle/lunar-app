package com.lunar.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "ticket_code", nullable = false, unique = true)
    private String ticketCode;
    
    @NotBlank
    @Size(max = 1000)
    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode;
    
    @Size(max = 500)
    @Column(name = "qr_code_image_url")
    private String qrCodeImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;
    
    @Column(name = "seat_number")
    private String seatNumber;
    
    @Column(name = "section")
    private String section;
    
    @Column(name = "row_number")
    private String rowNumber;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "transfer_to_user_id")
    private Long transferToUserId;
    
    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;
    
    @Column(name = "transfer_notes", columnDefinition = "TEXT")
    private String transferNotes;
    
    @Column(name = "is_used")
    private Boolean isUsed = false;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "used_by")
    private String usedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public enum TicketStatus {
        ACTIVE, USED, CANCELLED, TRANSFERRED, EXPIRED
    }
    
    // Helper methods
    public boolean isActive() {
        return status == TicketStatus.ACTIVE;
    }
    
    public boolean isUsed() {
        return status == TicketStatus.USED || isUsed;
    }
    
    public boolean isTransferable() {
        return ticketType.getIsTransferable() && 
               status == TicketStatus.ACTIVE && 
               !isUsed();
    }
    
    public boolean canBeUsed() {
        return status == TicketStatus.ACTIVE && 
               !isUsed() && 
               booking.getStatus() == Booking.BookingStatus.CONFIRMED;
    }
    
    public void markAsUsed(String usedBy) {
        this.status = TicketStatus.USED;
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
        this.usedBy = usedBy;
        this.checkInTime = LocalDateTime.now();
    }
    
    public void transferTo(Long newUserId, String notes) {
        this.transferToUserId = newUserId;
        this.transferredAt = LocalDateTime.now();
        this.transferNotes = notes;
        this.status = TicketStatus.TRANSFERRED;
    }
    
    public void generateTicketCode() {
        if (this.ticketCode == null) {
            this.ticketCode = "TKT-" + 
                System.currentTimeMillis() + "-" + 
                String.format("%06d", (int) (Math.random() * 1000000));
        }
    }
    
    public void generateQrCode() {
        if (this.qrCode == null) {
            this.qrCode = "QR-" + 
                booking.getId() + "-" + 
                id + "-" + 
                System.currentTimeMillis();
        }
    }
}
