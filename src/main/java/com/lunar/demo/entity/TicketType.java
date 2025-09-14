package com.lunar.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
@Table(name = "ticket_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;
    
    @Size(max = 500)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull
    @Min(value = 1)
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;
    
    @Column(name = "quantity_sold")
    private Integer quantitySold = 0;
    
    @Column(name = "max_quantity_per_booking")
    private Integer maxQuantityPerBooking;
    
    @Column(name = "min_quantity_per_booking")
    private Integer minQuantityPerBooking = 1;
    
    @Column(name = "is_transferable")
    private Boolean isTransferable = true;
    
    @Column(name = "is_refundable")
    private Boolean isRefundable = true;
    
    @Column(name = "refund_deadline")
    private LocalDateTime refundDeadline;
    
    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;
    
    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;
    
    @Column(name = "is_early_bird")
    private Boolean isEarlyBird = false;
    
    @Column(name = "early_bird_discount_percentage")
    private BigDecimal earlyBirdDiscountPercentage;
    
    @Column(name = "early_bird_end_date")
    private LocalDateTime earlyBirdEndDate;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;
    
    @Size(max = 1000)
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @OneToMany(mappedBy = "ticketType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BookingItem> bookingItems = new HashSet<>();
    
    public enum TicketStatus {
        ACTIVE, INACTIVE, SOLD_OUT, CANCELLED
    }
    
    // Helper methods
    public boolean isAvailable() {
        return status == TicketStatus.ACTIVE && quantitySold < quantityAvailable;
    }
    
    public boolean isOnSale() {
        LocalDateTime now = LocalDateTime.now();
        return (saleStartDate == null || now.isAfter(saleStartDate)) &&
               (saleEndDate == null || now.isBefore(saleEndDate));
    }
    
    public boolean isEarlyBirdActive() {
        if (!isEarlyBird || earlyBirdEndDate == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(earlyBirdEndDate);
    }
    
    public BigDecimal getEffectivePrice() {
        if (isEarlyBirdActive() && earlyBirdDiscountPercentage != null) {
            BigDecimal discountAmount = price.multiply(earlyBirdDiscountPercentage).divide(new BigDecimal("100"));
            return price.subtract(discountAmount);
        }
        return price;
    }
    
    public int getRemainingQuantity() {
        return quantityAvailable - quantitySold;
    }
    
    public boolean isSoldOut() {
        return quantitySold >= quantityAvailable;
    }
    
    public boolean canPurchase(int quantity) {
        return isAvailable() && 
               isOnSale() && 
               quantity >= minQuantityPerBooking &&
               (maxQuantityPerBooking == null || quantity <= maxQuantityPerBooking) &&
               quantity <= getRemainingQuantity();
    }
}
