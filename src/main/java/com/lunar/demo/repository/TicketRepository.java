package com.lunar.demo.repository;

import com.lunar.demo.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Optional<Ticket> findByTicketCode(String ticketCode);
    
    Optional<Ticket> findByQrCode(String qrCode);
    
    List<Ticket> findByUserId(Long userId);
    
    List<Ticket> findByBookingId(Long bookingId);
    
    List<Ticket> findByTicketTypeId(Long ticketTypeId);
    
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId AND t.status = :status")
    List<Ticket> findByUserAndStatus(@Param("userId") Long userId, 
                                    @Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.booking.id = :bookingId AND t.status = :status")
    List<Ticket> findByBookingAndStatus(@Param("bookingId") Long bookingId, 
                                       @Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.status = 'ACTIVE'")
    List<Ticket> findActiveTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.status = 'USED'")
    List<Ticket> findUsedTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.startDate >= :startDate AND t.booking.event.startDate <= :endDate")
    List<Ticket> findTicketsByEventDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.checkInTime IS NOT NULL")
    List<Ticket> findCheckedInTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.checkInTime IS NULL AND t.booking.event.startDate < :currentDate")
    List<Ticket> findNoShowTicketsByEvent(@Param("eventId") Long eventId,
                                         @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.id = :userId")
    long countByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.booking.id = :bookingId")
    long countByBooking(@Param("bookingId") Long bookingId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.booking.event.id = :eventId")
    long countByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.status = 'USED'")
    long countUsedTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.status = 'ACTIVE'")
    long countActiveTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.transferToUserId = :userId AND t.status = 'TRANSFERRED'")
    List<Ticket> findTransferredTicketsToUser(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.user.id = :userId AND t.status = 'TRANSFERRED'")
    List<Ticket> findTransferredTicketsFromUser(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.seatNumber = :seatNumber")
    Optional<Ticket> findByEventAndSeatNumber(@Param("eventId") Long eventId,
                                             @Param("seatNumber") String seatNumber);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.booking.event.id = :eventId AND t.section = :section")
    List<Ticket> findByEventAndSection(@Param("eventId") Long eventId,
                                      @Param("section") String section);
}
