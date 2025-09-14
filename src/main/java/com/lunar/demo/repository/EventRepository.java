package com.lunar.demo.repository;

import com.lunar.demo.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByOrganizerId(Long organizerId);
    
    List<Event> findByCategory(Event.EventCategory category);
    
    List<Event> findByStatus(Event.EventStatus status);
    
    List<Event> findByIsFeaturedTrue();
    
    List<Event> findByIsPublicTrue();
    
    Page<Event> findByStatusAndIsPublicTrue(Event.EventStatus status, Pageable pageable);
    
    Page<Event> findByCategoryAndStatusAndIsPublicTrue(Event.EventCategory category, 
                                                     Event.EventStatus status, 
                                                     Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.status = :status")
    List<Event> findByOrganizerAndStatus(@Param("organizerId") Long organizerId, 
                                        @Param("status") Event.EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.startDate >= :startDate AND e.startDate <= :endDate")
    List<Event> findUpcomingEvents(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.startDate >= :currentDate AND " +
           "(e.registrationStartDate IS NULL OR e.registrationStartDate <= :currentDate) AND " +
           "(e.registrationEndDate IS NULL OR e.registrationEndDate >= :currentDate)")
    List<Event> findActiveEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Event> searchEvents(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.city = :city AND e.startDate >= :currentDate")
    List<Event> findEventsByCity(@Param("city") String city, 
                                @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.isFeatured = true AND e.startDate >= :currentDate")
    List<Event> findFeaturedEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.startDate BETWEEN :startDate AND :endDate")
    Page<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.maxAttendees IS NOT NULL AND e.currentAttendees >= e.maxAttendees")
    List<Event> findSoldOutEvents();
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = 'PUBLISHED' AND e.isPublic = true AND " +
           "e.startDate < :currentDate")
    List<Event> findPastEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE " +
           "e.organizer.id = :organizerId AND " +
           "e.startDate BETWEEN :startDate AND :endDate")
    List<Event> findOrganizerEventsInDateRange(@Param("organizerId") Long organizerId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId")
    long countByOrganizer(@Param("organizerId") Long organizerId);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.status = :status")
    long countByStatus(@Param("status") Event.EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.isFeatured = true ORDER BY e.createdAt DESC")
    List<Event> findRecentlyFeaturedEvents();
}
