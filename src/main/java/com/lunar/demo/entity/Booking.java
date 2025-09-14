package com.lunar.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "service_fee", precision = 10, scale = 2)
    private BigDecimal serviceFee = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    private String currency = "USD";
    
    @Column(name = "booking_notes", columnDefinition = "TEXT")
    private String bookingNotes;
    
    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_processed_at")
    private LocalDateTime refundProcessedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BookingItem> bookingItems = new HashSet<>();
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Ticket> tickets = new HashSet<>();
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, REFUNDED, CHECKED_IN, NO_SHOW, EXPIRED
    }
    
    // Helper methods
    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }
    
    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED || status == BookingStatus.REFUNDED;
    }
    
    public boolean isCheckedIn() {
        return status == BookingStatus.CHECKED_IN;
    }
    
    public boolean canBeCancelled() {
        return (status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED) &&
               event.getRegistrationEndDate() != null &&
               LocalDateTime.now().isBefore(event.getRegistrationEndDate());
    }
    
    public boolean canBeRefunded() {
        return isCancelled() && 
               event.getCancellationPolicy() != null &&
               !event.getCancellationPolicy().isEmpty();
    }
    
    public int getTotalTickets() {
        return bookingItems.stream()
                .mapToInt(BookingItem::getQuantity)
                .sum();
    }
    
    public BigDecimal getNetAmount() {
        return totalAmount.subtract(discountAmount).add(taxAmount).add(serviceFee);
    }
    
    public void generateBookingReference() {
        if (this.bookingReference == null) {
            this.bookingReference = "LUNAR-" + 
                System.currentTimeMillis() + "-" + 
                String.format("%04d", (int) (Math.random() * 10000));
        }
    }
}
