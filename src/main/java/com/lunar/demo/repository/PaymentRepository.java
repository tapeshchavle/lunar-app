package com.lunar.demo.repository;

import com.lunar.demo.entity.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    Optional<Payment> findByExternalPaymentId(String externalPaymentId);
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByBookingId(Long bookingId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    
    Page<Payment> findByBookingId(Long bookingId, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = :status")
    List<Payment> findByUserAndStatus(@Param("userId") Long userId, 
                                     @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.booking.id = :bookingId AND p.status = :status")
    List<Payment> findByBookingAndStatus(@Param("bookingId") Long bookingId, 
                                        @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.user.id = :userId AND p.createdAt >= :fromDate AND p.createdAt <= :toDate")
    List<Payment> findUserPaymentsInDateRange(@Param("userId") Long userId,
                                             @Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.booking.event.id = :eventId AND p.createdAt >= :fromDate AND p.createdAt <= :toDate")
    List<Payment> findEventPaymentsInDateRange(@Param("eventId") Long eventId,
                                              @Param("fromDate") LocalDateTime fromDate,
                                              @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND p.createdAt >= :fromDate AND p.createdAt <= :toDate")
    List<Payment> findCompletedPaymentsInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                                  @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.status = 'PENDING' AND p.createdAt < :expiryDate")
    List<Payment> findExpiredPendingPayments(@Param("expiryDate") LocalDateTime expiryDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.paymentMethod = :paymentMethod AND p.status = 'COMPLETED'")
    List<Payment> findByPaymentMethodAndCompleted(@Param("paymentMethod") Payment.PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.paymentGateway = :gateway AND p.status = 'COMPLETED'")
    List<Payment> findByPaymentGatewayAndCompleted(@Param("gateway") String gateway);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.user.id = :userId")
    long countByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.booking.id = :bookingId")
    long countByBooking(@Param("bookingId") Long bookingId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND p.user.id = :userId")
    Double getTotalPaidByUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND p.booking.event.id = :eventId")
    Double getTotalRevenueByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND p.createdAt >= :fromDate AND p.createdAt <= :toDate")
    Double getTotalRevenueInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                     @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE " +
           "p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') AND p.user.id = :userId")
    Double getTotalRefundedByUser(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.paymentReference LIKE CONCAT('%', :searchTerm, '%') OR " +
           "p.externalPaymentId LIKE CONCAT('%', :searchTerm, '%') OR " +
           "p.user.firstName LIKE CONCAT('%', :searchTerm, '%') OR " +
           "p.user.lastName LIKE CONCAT('%', :searchTerm, '%')")
    Page<Payment> searchPayments(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.booking.event.organizer.id = :organizerId")
    List<Payment> findByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.booking.event.organizer.id = :organizerId AND p.status = :status")
    List<Payment> findByOrganizerAndStatus(@Param("organizerId") Long organizerId,
                                          @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "p.webhookReceivedAt IS NOT NULL AND p.webhookReceivedAt >= :fromDate")
    List<Payment> findPaymentsWithWebhooks(@Param("fromDate") LocalDateTime fromDate);
}
