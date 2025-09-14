package com.lunar.demo.repository;

import com.lunar.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByEventId(Long eventId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    
    Page<Booking> findByEventId(Long eventId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserAndStatus(@Param("userId") Long userId, 
                                     @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId AND b.status = :status")
    List<Booking> findByEventAndStatus(@Param("eventId") Long eventId, 
                                      @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.user.id = :userId AND b.createdAt >= :fromDate AND b.createdAt <= :toDate")
    List<Booking> findUserBookingsInDateRange(@Param("userId") Long userId,
                                             @Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.event.id = :eventId AND b.createdAt >= :fromDate AND b.createdAt <= :toDate")
    List<Booking> findEventBookingsInDateRange(@Param("eventId") Long eventId,
                                              @Param("fromDate") LocalDateTime fromDate,
                                              @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.status = 'CONFIRMED' AND b.event.startDate >= :currentDate")
    List<Booking> findUpcomingConfirmedBookings(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.status = 'CONFIRMED' AND b.event.startDate < :currentDate")
    List<Booking> findPastConfirmedBookings(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.status = 'PENDING' AND b.createdAt < :expiryDate")
    List<Booking> findExpiredPendingBookings(@Param("expiryDate") LocalDateTime expiryDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId")
    long countByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId")
    long countByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    long countByStatus(@Param("status") Booking.BookingStatus status);
    
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE " +
           "b.status = 'CONFIRMED' AND b.event.id = :eventId")
    Double getTotalRevenueByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE " +
           "b.status = 'CONFIRMED' AND b.user.id = :userId")
    Double getTotalSpentByUser(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.bookingReference LIKE CONCAT('%', :searchTerm, '%') OR " +
           "b.user.firstName LIKE CONCAT('%', :searchTerm, '%') OR " +
           "b.user.lastName LIKE CONCAT('%', :searchTerm, '%') OR " +
           "b.user.email LIKE CONCAT('%', :searchTerm, '%')")
    Page<Booking> searchBookings(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.event.organizer.id = :organizerId")
    List<Booking> findByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "b.event.organizer.id = :organizerId AND b.status = :status")
    List<Booking> findByOrganizerAndStatus(@Param("organizerId") Long organizerId,
                                          @Param("status") Booking.BookingStatus status);
}
