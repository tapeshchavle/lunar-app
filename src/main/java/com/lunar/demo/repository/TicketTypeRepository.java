package com.lunar.demo.repository;

import com.lunar.demo.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    
    /**
     * Find ticket types by event ID
     */
    List<TicketType> findByEventId(Long eventId);
    
    /**
     * Find ticket type by event ID and name
     */
    Optional<TicketType> findByEventIdAndName(Long eventId, String name);
    
    /**
     * Find active ticket types by event ID
     */
    List<TicketType> findByEventIdAndIsActiveTrue(Long eventId);
    
    /**
     * Check if ticket type exists by event ID and name
     */
    boolean existsByEventIdAndName(Long eventId, String name);
}
