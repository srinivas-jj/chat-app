package com.chatapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chatapp.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.isDeleted = false ORDER BY m.sentAt DESC")
    Page<Message> findByRoomIdOrderBySentAtDesc(@Param("roomId") Long roomId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.isDeleted = false ORDER BY m.sentAt ASC")
    List<Message> findByRoomIdOrderBySentAtAsc(@Param("roomId") Long roomId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.room.id = :roomId AND m.isDeleted = false")
    Long countMessagesByRoomId(@Param("roomId") Long roomId);
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.content LIKE %:searchTerm% AND m.isDeleted = false")
    List<Message> searchMessagesInRoom(@Param("roomId") Long roomId, @Param("searchTerm") String searchTerm);
} 