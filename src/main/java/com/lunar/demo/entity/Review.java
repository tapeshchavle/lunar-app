package com.lunar.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Size(max = 1000)
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "is_public")
    private Boolean isPublic = true;
    
    @Column(name = "is_approved")
    private Boolean isApproved = true;
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
    
    @Column(name = "not_helpful_count")
    private Integer notHelpfulCount = 0;
    
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "response_by")
    private String responseBy;
    
    @Column(name = "response_at")
    private LocalDateTime responseAt;
    
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
    
    // Helper methods
    public boolean isHelpful() {
        return helpfulCount > notHelpfulCount;
    }
    
    public double getHelpfulnessScore() {
        int total = helpfulCount + notHelpfulCount;
        return total > 0 ? (double) helpfulCount / total : 0.0;
    }
    
    public void markAsHelpful() {
        this.helpfulCount++;
    }
    
    public void markAsNotHelpful() {
        this.notHelpfulCount++;
    }
    
    public void addResponse(String response, String responseBy) {
        this.response = response;
        this.responseBy = responseBy;
        this.responseAt = LocalDateTime.now();
    }
}
