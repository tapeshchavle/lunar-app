package com.lunar.demo.controller;

import com.lunar.demo.dto.EventCreateRequest;
import com.lunar.demo.dto.EventResponse;
import com.lunar.demo.dto.EventSearchRequest;
import com.lunar.demo.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        log.info("Event creation attempt: {}", request.getTitle());
        EventResponse event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        log.info("Get event by ID: {}", id);
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(Pageable pageable) {
        log.info("Get all events request");
        Page<EventResponse> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<EventResponse>> searchEvents(@RequestBody EventSearchRequest request, 
                                                          Pageable pageable) {
        log.info("Event search request: {}", request.getSearchTerm());
        Page<EventResponse> events = eventService.searchEvents(request, pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<EventResponse>> getFeaturedEvents() {
        log.info("Get featured events request");
        List<EventResponse> events = eventService.getFeaturedEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        log.info("Get upcoming events request");
        List<EventResponse> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<Page<EventResponse>> getEventsByOrganizer(@PathVariable Long organizerId, 
                                                                  Pageable pageable) {
        log.info("Get events by organizer: {}", organizerId);
        Page<EventResponse> events = eventService.getEventsByOrganizer(organizerId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, 
                                                   @Valid @RequestBody EventCreateRequest request) {
        log.info("Event update attempt for ID: {}", id);
        EventResponse event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(event);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("Event deletion attempt for ID: {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id) {
        log.info("Event publish attempt for ID: {}", id);
        EventResponse event = eventService.publishEvent(id);
        return ResponseEntity.ok(event);
    }
}
