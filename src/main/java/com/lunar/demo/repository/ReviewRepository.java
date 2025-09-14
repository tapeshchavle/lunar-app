package com.lunar.demo.repository;

import com.lunar.demo.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByUserId(Long userId);
    
    List<Review> findByEventId(Long eventId);
    
    List<Review> findByRating(Integer rating);
    
    List<Review> findByIsApprovedTrue();
    
    List<Review> findByIsPublicTrue();
    
    Page<Review> findByEventIdAndIsApprovedTrueAndIsPublicTrue(Long eventId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.isApproved = true")
    List<Review> findByUserAndApproved(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Review r WHERE r.event.id = :eventId AND r.isApproved = true AND r.isPublic = true")
    List<Review> findApprovedPublicReviewsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT r FROM Review r WHERE r.event.id = :eventId AND r.rating = :rating")
    List<Review> findByEventAndRating(@Param("eventId") Long eventId, @Param("rating") Integer rating);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId AND r.isApproved = true AND r.isPublic = true")
    Double getAverageRatingByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.event.id = :eventId AND r.isApproved = true AND r.isPublic = true")
    long countApprovedReviewsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.event.id = :eventId AND r.rating = :rating AND r.isApproved = true AND r.isPublic = true")
    long countByEventAndRating(@Param("eventId") Long eventId, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.id = :eventId AND r.isApproved = true AND r.isPublic = true " +
           "ORDER BY r.helpfulCount DESC, r.createdAt DESC")
    List<Review> findTopHelpfulReviewsByEvent(@Param("eventId") Long eventId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.id = :eventId AND r.isApproved = true AND r.isPublic = true " +
           "ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByEvent(@Param("eventId") Long eventId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.user.id = :userId AND r.isVerifiedPurchase = true")
    List<Review> findVerifiedReviewsByUser(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.isApproved = false")
    List<Review> findPendingApprovalReviews();
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.id = :eventId AND r.response IS NOT NULL")
    List<Review> findReviewsWithResponseByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.helpfulCount > r.notHelpfulCount")
    List<Review> findHelpfulReviews();
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.helpfulCount > 0 OR r.notHelpfulCount > 0")
    List<Review> findReviewsWithFeedback();
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.id = :eventId AND " +
           "(LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Review> searchReviewsByEvent(@Param("eventId") Long eventId, 
                                     @Param("searchTerm") String searchTerm, 
                                     Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.organizer.id = :organizerId")
    List<Review> findByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.organizer.id = :organizerId AND r.isApproved = true")
    List<Review> findApprovedReviewsByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.organizer.id = :organizerId AND r.isApproved = false")
    List<Review> findPendingReviewsByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.event.id = :eventId AND r.user.id = :userId")
    Optional<Review> findByEventAndUser(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
