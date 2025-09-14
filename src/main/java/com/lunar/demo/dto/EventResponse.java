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
public class EventResponse {
    
    private Long id;
    private String title;
    private String description;
    private String detailedDescription;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime registrationStartDate;
    private LocalDateTime registrationEndDate;
    private String venueName;
    private String venueAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private String category;
    private String status;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private Boolean isOnline;
    private String onlineMeetingUrl;
    private String imageUrl;
    private String bannerUrl;
    private Boolean isFeatured;
    private Boolean isPublic;
    private Boolean requiresApproval;
    private Integer ageRestriction;
    private String termsAndConditions;
    private String cancellationPolicy;
    private Long organizerId;
    private String organizerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
