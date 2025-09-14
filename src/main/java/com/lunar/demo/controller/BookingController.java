package com.lunar.demo.controller;

import com.lunar.demo.dto.BookingCreateRequest;
import com.lunar.demo.dto.BookingResponse;
import com.lunar.demo.service.BookingService;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        log.info("Booking creation attempt for event: {}", request.getEventId());
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        log.info("Get booking by ID: {}", id);
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
    
    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(@PathVariable String reference) {
        log.info("Get booking by reference: {}", reference);
        BookingResponse booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(booking);
    }
    
    @GetMapping
    public ResponseEntity<Page<BookingResponse>> getUserBookings(Pageable pageable) {
        log.info("Get user bookings request");
        Page<BookingResponse> bookings = bookingService.getUserBookings(pageable);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getEventBookings(@PathVariable Long eventId) {
        log.info("Get bookings for event: {}", eventId);
        List<BookingResponse> bookings = bookingService.getEventBookings(eventId);
        return ResponseEntity.ok(bookings);
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        log.info("Booking confirmation attempt for ID: {}", id);
        BookingResponse booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(booking);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, 
                                                       @RequestParam String reason) {
        log.info("Booking cancellation attempt for ID: {}", id);
        BookingResponse booking = bookingService.cancelBooking(id, reason);
        return ResponseEntity.ok(booking);
    }
    
    @PostMapping("/{id}/checkin")
    public ResponseEntity<BookingResponse> checkInBooking(@PathVariable Long id) {
        log.info("Booking check-in attempt for ID: {}", id);
        BookingResponse booking = bookingService.checkInBooking(id);
        return ResponseEntity.ok(booking);
    }
}
