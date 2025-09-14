package com.lunar.demo.controller;

import com.lunar.demo.dto.TicketResponse;
import com.lunar.demo.entity.Ticket;
import com.lunar.demo.repository.TicketRepository;
import com.lunar.demo.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {
    
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    
    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getUserTickets(Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        log.info("Get tickets for user: {}", userId);
        Page<Ticket> tickets = ticketRepository.findByUserId(userId, pageable);
        Page<TicketResponse> ticketResponses = tickets.map(this::mapToTicketResponse);
        return ResponseEntity.ok(ticketResponses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        log.info("Get ticket by ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ResponseEntity.ok(mapToTicketResponse(ticket));
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<TicketResponse>> getBookingTickets(@PathVariable Long bookingId) {
        log.info("Get tickets for booking: {}", bookingId);
        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(this::mapToTicketResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ticketResponses);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<TicketResponse> validateTicket(@RequestParam String qrCode) {
        log.info("Ticket validation attempt for QR code: {}", qrCode);
        
        // Parse QR code data
        String[] parts = qrCode.split("\\|");
        if (parts.length != 4 || !"LUNAR_TICKET".equals(parts[0])) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Long bookingId = Long.parseLong(parts[1]);
            Long ticketId = Long.parseLong(parts[2]);
            
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
            if (!ticket.getBooking().getId().equals(bookingId)) {
                return ResponseEntity.badRequest().build();
            }
            
            if (!ticket.canBeUsed()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            return ResponseEntity.ok(mapToTicketResponse(ticket));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/use")
    public ResponseEntity<TicketResponse> useTicket(@PathVariable Long id, 
                                                  @RequestParam String usedBy) {
        log.info("Ticket usage attempt for ID: {} by: {}", id, usedBy);
        
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.canBeUsed()) {
            throw new RuntimeException("Ticket cannot be used");
        }
        
        ticket.markAsUsed(usedBy);
        ticket = ticketRepository.save(ticket);
        
        return ResponseEntity.ok(mapToTicketResponse(ticket));
    }
    
    @PostMapping("/{id}/transfer")
    public ResponseEntity<TicketResponse> transferTicket(@PathVariable Long id,
                                                       @RequestParam Long newUserId,
                                                       @RequestParam String notes) {
        log.info("Ticket transfer attempt for ID: {} to user: {}", id, newUserId);
        
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.isTransferable()) {
            throw new RuntimeException("Ticket is not transferable");
        }
        
        ticket.transferTo(newUserId, notes);
        ticket = ticketRepository.save(ticket);
        
        return ResponseEntity.ok(mapToTicketResponse(ticket));
    }
    
    @GetMapping("/qr/{id}")
    public ResponseEntity<String> getTicketQrCode(@PathVariable Long id) {
        log.info("Get QR code for ticket ID: {}", id);
        
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        String qrCode = qrCodeService.generateQrCodeForTicket(
                ticket.getBooking().getId(), 
                ticket.getId(), 
                ticket.getTicketCode()
        );
        
        return ResponseEntity.ok(qrCode);
    }
    
    private TicketResponse mapToTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .qrCode(ticket.getQrCode())
                .qrCodeImageUrl(ticket.getQrCodeImageUrl())
                .status(ticket.getStatus().name())
                .seatNumber(ticket.getSeatNumber())
                .section(ticket.getSection())
                .rowNumber(ticket.getRowNumber())
                .checkInTime(ticket.getCheckInTime())
                .checkOutTime(ticket.getCheckOutTime())
                .transferToUserId(ticket.getTransferToUserId())
                .transferredAt(ticket.getTransferredAt())
                .transferNotes(ticket.getTransferNotes())
                .isUsed(ticket.getIsUsed())
                .usedAt(ticket.getUsedAt())
                .usedBy(ticket.getUsedBy())
                .bookingId(ticket.getBooking().getId())
                .ticketTypeId(ticket.getTicketType().getId())
                .ticketTypeName(ticket.getTicketType().getName())
                .userId(ticket.getUser().getId())
                .userName(ticket.getUser().getFullName())
                .eventId(ticket.getBooking().getEvent().getId())
                .eventTitle(ticket.getBooking().getEvent().getTitle())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
