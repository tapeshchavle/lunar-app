package com.lunar.demo.service;

import com.lunar.demo.dto.EventCreateRequest;
import com.lunar.demo.dto.EventResponse;
import com.lunar.demo.dto.EventSearchRequest;
import com.lunar.demo.entity.Event;
import com.lunar.demo.entity.User;
import com.lunar.demo.repository.EventRepository;
import com.lunar.demo.repository.UserRepository;
import com.lunar.demo.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User organizer = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .detailedDescription(request.getDetailedDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationStartDate(request.getRegistrationStartDate())
                .registrationEndDate(request.getRegistrationEndDate())
                .venueName(request.getVenueName())
                .venueAddress(request.getVenueAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .category(request.getCategory())
                .status(Event.EventStatus.DRAFT)
                .maxAttendees(request.getMaxAttendees())
                .isOnline(request.getIsOnline())
                .onlineMeetingUrl(request.getOnlineMeetingUrl())
                .imageUrl(request.getImageUrl())
                .bannerUrl(request.getBannerUrl())
                .isFeatured(request.getIsFeatured())
                .isPublic(request.getIsPublic())
                .requiresApproval(request.getRequiresApproval())
                .ageRestriction(request.getAgeRestriction())
                .termsAndConditions(request.getTermsAndConditions())
                .cancellationPolicy(request.getCancellationPolicy())
                .organizer(organizer)
                .build();
        
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        
        return mapToEventResponse(savedEvent);
    }
    
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        return mapToEventResponse(event);
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findByStatusAndIsPublicTrue(Event.EventStatus.PUBLISHED, pageable);
        return events.map(this::mapToEventResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> searchEvents(EventSearchRequest request, Pageable pageable) {
        Page<Event> events;
        
        if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
            events = eventRepository.searchEvents(request.getSearchTerm(), pageable);
        } else if (request.getCategory() != null) {
            events = eventRepository.findByCategoryAndStatusAndIsPublicTrue(
                    request.getCategory(), Event.EventStatus.PUBLISHED, pageable);
        } else if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            List<Event> cityEvents = eventRepository.findEventsByCity(request.getCity(), LocalDateTime.now());
            events = Page.empty(pageable);
        } else {
            events = eventRepository.findByStatusAndIsPublicTrue(Event.EventStatus.PUBLISHED, pageable);
        }
        
        return events.map(this::mapToEventResponse);
    }
    
    @Transactional(readOnly = true)
    public List<EventResponse> getFeaturedEvents() {
        List<Event> events = eventRepository.findFeaturedEvents(LocalDateTime.now());
        return events.stream()
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = now.plusMonths(1);
        List<Event> events = eventRepository.findUpcomingEvents(now, nextMonth);
        return events.stream()
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByOrganizer(Long organizerId, Pageable pageable) {
        List<Event> eventList = eventRepository.findByOrganizerId(organizerId);
        // Convert to Page manually or use a different approach
        return Page.empty(pageable);
    }
    
    @Transactional
    public EventResponse updateEvent(Long id, EventCreateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(userPrincipal.getId()) && 
            !userPrincipal.getRole().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to update this event");
        }
        
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDetailedDescription(request.getDetailedDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setRegistrationStartDate(request.getRegistrationStartDate());
        event.setRegistrationEndDate(request.getRegistrationEndDate());
        event.setVenueName(request.getVenueName());
        event.setVenueAddress(request.getVenueAddress());
        event.setCity(request.getCity());
        event.setState(request.getState());
        event.setCountry(request.getCountry());
        event.setPostalCode(request.getPostalCode());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setCategory(request.getCategory());
        event.setMaxAttendees(request.getMaxAttendees());
        event.setIsOnline(request.getIsOnline());
        event.setOnlineMeetingUrl(request.getOnlineMeetingUrl());
        event.setImageUrl(request.getImageUrl());
        event.setBannerUrl(request.getBannerUrl());
        event.setIsFeatured(request.getIsFeatured());
        event.setIsPublic(request.getIsPublic());
        event.setRequiresApproval(request.getRequiresApproval());
        event.setAgeRestriction(request.getAgeRestriction());
        event.setTermsAndConditions(request.getTermsAndConditions());
        event.setCancellationPolicy(request.getCancellationPolicy());
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with ID: {}", updatedEvent.getId());
        
        return mapToEventResponse(updatedEvent);
    }
    
    @Transactional
    public void deleteEvent(Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(userPrincipal.getId()) && 
            !userPrincipal.getRole().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to delete this event");
        }
        
        eventRepository.delete(event);
        log.info("Event deleted successfully with ID: {}", id);
    }
    
    @Transactional
    public EventResponse publishEvent(Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(userPrincipal.getId()) && 
            !userPrincipal.getRole().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to publish this event");
        }
        
        event.setStatus(Event.EventStatus.PUBLISHED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event published successfully with ID: {}", updatedEvent.getId());
        
        return mapToEventResponse(updatedEvent);
    }
    
    private EventResponse mapToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .detailedDescription(event.getDetailedDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .registrationStartDate(event.getRegistrationStartDate())
                .registrationEndDate(event.getRegistrationEndDate())
                .venueName(event.getVenueName())
                .venueAddress(event.getVenueAddress())
                .city(event.getCity())
                .state(event.getState())
                .country(event.getCountry())
                .postalCode(event.getPostalCode())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .category(event.getCategory().name())
                .status(event.getStatus().name())
                .maxAttendees(event.getMaxAttendees())
                .currentAttendees(event.getCurrentAttendees())
                .isOnline(event.getIsOnline())
                .onlineMeetingUrl(event.getOnlineMeetingUrl())
                .imageUrl(event.getImageUrl())
                .bannerUrl(event.getBannerUrl())
                .isFeatured(event.getIsFeatured())
                .isPublic(event.getIsPublic())
                .requiresApproval(event.getRequiresApproval())
                .ageRestriction(event.getAgeRestriction())
                .termsAndConditions(event.getTermsAndConditions())
                .cancellationPolicy(event.getCancellationPolicy())
                .organizerId(event.getOrganizer().getId())
                .organizerName(event.getOrganizer().getFullName())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
