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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;
    
    @Size(max = 1000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Size(max = 2000)
    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;
    
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "registration_start_date")
    private LocalDateTime registrationStartDate;
    
    @Column(name = "registration_end_date")
    private LocalDateTime registrationEndDate;
    
    @NotBlank
    @Size(max = 200)
    @Column(name = "venue_name", nullable = false)
    private String venueName;
    
    @NotBlank
    @Size(max = 500)
    @Column(name = "venue_address", nullable = false)
    private String venueAddress;
    
    @Size(max = 100)
    @Column(name = "city")
    private String city;
    
    @Size(max = 100)
    @Column(name = "state")
    private String state;
    
    @Size(max = 100)
    @Column(name = "country")
    private String country;
    
    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private EventCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;
    
    @Column(name = "max_attendees")
    private Integer maxAttendees;
    
    @Column(name = "current_attendees")
    private Integer currentAttendees = 0;
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Size(max = 500)
    @Column(name = "online_meeting_url")
    private String onlineMeetingUrl;
    
    @Size(max = 500)
    @Column(name = "image_url")
    private String imageUrl;
    
    @Size(max = 500)
    @Column(name = "banner_url")
    private String bannerUrl;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_public")
    private Boolean isPublic = true;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;
    
    @Column(name = "age_restriction")
    private Integer ageRestriction;
    
    @Size(max = 1000)
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
    
    @Size(max = 1000)
    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketType> ticketTypes = new HashSet<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventTag> tags = new HashSet<>();
    
    public enum EventCategory {
        CONFERENCE, WORKSHOP, SEMINAR, CONCERT, FESTIVAL, SPORTS, 
        NETWORKING, EXHIBITION, WEBINAR, MEETUP, CONVENTION, 
        GALA, AWARD_CEREMONY, PRODUCT_LAUNCH, TRAINING, OTHER
    }
    
    public enum EventStatus {
        DRAFT, PUBLISHED, CANCELLED, COMPLETED, POSTPONED, SOLD_OUT
    }
    
    // Helper methods
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return status == EventStatus.PUBLISHED && 
               (registrationStartDate == null || now.isAfter(registrationStartDate)) &&
               (registrationEndDate == null || now.isBefore(registrationEndDate));
    }
    
    public boolean isSoldOut() {
        return maxAttendees != null && currentAttendees >= maxAttendees;
    }
    
    public boolean isEventActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }
    
    public boolean isEventUpcoming() {
        return LocalDateTime.now().isBefore(startDate);
    }
    
    public boolean isEventPast() {
        return LocalDateTime.now().isAfter(endDate);
    }
}
