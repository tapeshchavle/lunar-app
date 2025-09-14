package com.lunar.demo.dto;

import com.lunar.demo.entity.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 2000, message = "Detailed description must not exceed 2000 characters")
    private String detailedDescription;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    private LocalDateTime registrationStartDate;
    private LocalDateTime registrationEndDate;
    
    @NotBlank(message = "Venue name is required")
    @Size(max = 200, message = "Venue name must not exceed 200 characters")
    private String venueName;
    
    @NotBlank(message = "Venue address is required")
    @Size(max = 500, message = "Venue address must not exceed 500 characters")
    private String venueAddress;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    private Double latitude;
    private Double longitude;
    
    @NotNull(message = "Category is required")
    private Event.EventCategory category;
    
    private Integer maxAttendees;
    private Boolean isOnline = false;
    
    @Size(max = 500, message = "Online meeting URL must not exceed 500 characters")
    private String onlineMeetingUrl;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    @Size(max = 500, message = "Banner URL must not exceed 500 characters")
    private String bannerUrl;
    
    private Boolean isFeatured = false;
    private Boolean isPublic = true;
    private Boolean requiresApproval = false;
    private Integer ageRestriction;
    
    @Size(max = 1000, message = "Terms and conditions must not exceed 1000 characters")
    private String termsAndConditions;
    
    @Size(max = 1000, message = "Cancellation policy must not exceed 1000 characters")
    private String cancellationPolicy;
}
