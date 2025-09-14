package com.lunar.demo.dto;

import com.lunar.demo.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchRequest {
    
    private String searchTerm;
    private Event.EventCategory category;
    private String city;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isOnline;
    private Boolean isFeatured;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy;
    private String sortDirection;
}
