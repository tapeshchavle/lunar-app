package com.lunar.demo.service;

import com.lunar.demo.dto.BookingCreateRequest;
import com.lunar.demo.dto.BookingResponse;
import com.lunar.demo.entity.*;
import com.lunar.demo.repository.BookingRepository;
import com.lunar.demo.repository.EventRepository;
import com.lunar.demo.repository.TicketRepository;
import com.lunar.demo.repository.TicketTypeRepository;
import com.lunar.demo.repository.UserRepository;
import com.lunar.demo.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Validate event is available for booking
        if (!event.isRegistrationOpen()) {
            throw new RuntimeException("Event registration is not open");
        }
        
        if (event.isSoldOut()) {
            throw new RuntimeException("Event is sold out");
        }
        
        // Create booking
        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .status(Booking.BookingStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .serviceFee(BigDecimal.ZERO)
                .currency("INR")
                .bookingNotes(request.getBookingNotes())
                .specialRequirements(request.getSpecialRequirements())
                .user(user)
                .event(event)
                .build();
        
        booking = bookingRepository.save(booking);
        
        // Process booking items
        List<BookingItem> bookingItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (BookingCreateRequest.TicketRequest ticketRequest : request.getTickets()) {
            TicketType ticketType = ticketTypeRepository.findById(ticketRequest.getTicketTypeId())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found"));
            
            // Validate ticket availability
            if (!ticketType.canPurchase(ticketRequest.getQuantity())) {
                throw new RuntimeException("Insufficient tickets available for " + ticketType.getName());
            }
            
            BigDecimal unitPrice = ticketType.getEffectivePrice();
            BigDecimal itemTotal = unitPrice.multiply(new BigDecimal(ticketRequest.getQuantity()));
            
            BookingItem bookingItem = BookingItem.builder()
                    .quantity(ticketRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .discountAmount(BigDecimal.ZERO)
                    .specialInstructions(ticketRequest.getSpecialInstructions())
                    .booking(booking)
                    .ticketType(ticketType)
                    .build();
            
            bookingItems.add(bookingItem);
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // Calculate service fee (2% of total amount)
        BigDecimal serviceFee = totalAmount.multiply(new BigDecimal("0.02"));
        BigDecimal taxAmount = totalAmount.multiply(new BigDecimal("0.18")); // 18% GST
        
        booking.setTotalAmount(totalAmount);
        booking.setServiceFee(serviceFee);
        booking.setTaxAmount(taxAmount);
        booking.setBookingItems(new HashSet<>(bookingItems));
        
        booking = bookingRepository.save(booking);
        
        // Generate tickets
        List<Ticket> tickets = generateTickets(booking);
        booking.setTickets(new HashSet<>(tickets));
        
        log.info("Booking created successfully with ID: {} and reference: {}", 
                booking.getId(), booking.getBookingReference());
        
        return mapToBookingResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        return mapToBookingResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        return mapToBookingResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingResponse> getUserBookings(Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Booking> bookings = bookingRepository.findByUserId(userPrincipal.getId(), pageable);
        return bookings.map(this::mapToBookingResponse);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getEventBookings(Long eventId) {
        List<Booking> bookings = bookingRepository.findByEventId(eventId);
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending status");
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        
        // Update event attendee count
        Event event = booking.getEvent();
        event.setCurrentAttendees(event.getCurrentAttendees() + booking.getTotalTickets());
        eventRepository.save(event);
        
        // Update ticket type quantities
        for (BookingItem item : booking.getBookingItems()) {
            TicketType ticketType = item.getTicketType();
            ticketType.setQuantitySold(ticketType.getQuantitySold() + item.getQuantity());
            ticketTypeRepository.save(ticketType);
        }
        
        log.info("Booking confirmed with ID: {}", bookingId);
        
        return mapToBookingResponse(booking);
    }
    
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        // Update event attendee count
        Event event = booking.getEvent();
        event.setCurrentAttendees(event.getCurrentAttendees() - booking.getTotalTickets());
        eventRepository.save(event);
        
        // Update ticket type quantities
        for (BookingItem item : booking.getBookingItems()) {
            TicketType ticketType = item.getTicketType();
            ticketType.setQuantitySold(ticketType.getQuantitySold() - item.getQuantity());
            ticketTypeRepository.save(ticketType);
        }
        
        log.info("Booking cancelled with ID: {}", bookingId);
        
        return mapToBookingResponse(booking);
    }
    
    @Transactional
    public BookingResponse checkInBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking is not confirmed");
        }
        
        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        booking.setCheckInTime(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        // Mark all tickets as used
        for (Ticket ticket : booking.getTickets()) {
            ticket.markAsUsed("SYSTEM");
            ticketRepository.save(ticket);
        }
        
        log.info("Booking checked in with ID: {}", bookingId);
        
        return mapToBookingResponse(booking);
    }
    
    private List<Ticket> generateTickets(Booking booking) {
        List<Ticket> tickets = new ArrayList<>();
        
        for (BookingItem item : booking.getBookingItems()) {
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = Ticket.builder()
                        .ticketCode(generateTicketCode())
                        .qrCode(generateQrCode(booking.getId(), item.getTicketType().getId()))
                        .status(Ticket.TicketStatus.ACTIVE)
                        .isUsed(false)
                        .booking(booking)
                        .ticketType(item.getTicketType())
                        .user(booking.getUser())
                        .build();
                
                tickets.add(ticket);
            }
        }
        
        return ticketRepository.saveAll(tickets);
    }
    
    private String generateBookingReference() {
        return "LUNAR-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", (int) (Math.random() * 10000));
    }
    
    private String generateTicketCode() {
        return "TKT-" + System.currentTimeMillis() + "-" + 
               String.format("%06d", (int) (Math.random() * 1000000));
    }
    
    private String generateQrCode(Long bookingId, Long ticketTypeId) {
        return "QR-" + bookingId + "-" + ticketTypeId + "-" + 
               System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .status(booking.getStatus().name())
                .totalAmount(booking.getTotalAmount())
                .discountAmount(booking.getDiscountAmount())
                .taxAmount(booking.getTaxAmount())
                .serviceFee(booking.getServiceFee())
                .netAmount(booking.getNetAmount())
                .currency(booking.getCurrency())
                .bookingNotes(booking.getBookingNotes())
                .specialRequirements(booking.getSpecialRequirements())
                .checkInTime(booking.getCheckInTime())
                .checkOutTime(booking.getCheckOutTime())
                .cancellationReason(booking.getCancellationReason())
                .cancelledAt(booking.getCancelledAt())
                .refundAmount(booking.getRefundAmount())
                .refundProcessedAt(booking.getRefundProcessedAt())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .totalTickets(booking.getTotalTickets())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
