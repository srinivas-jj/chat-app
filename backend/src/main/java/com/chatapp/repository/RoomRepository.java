package com.chatapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chatapp.entity.Room;
import com.chatapp.entity.RoomType;
import com.chatapp.entity.User;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    @Query("SELECT r FROM Room r JOIN r.members m WHERE m.id = :userId ORDER BY r.updatedAt DESC")
    List<Room> findRoomsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Room r WHERE r.type = :type AND r.members.size = 2 " +
           "AND :user1 MEMBER OF r.members AND :user2 MEMBER OF r.members")
    Optional<Room> findPrivateRoomBetweenUsers(@Param("type") RoomType type, 
                                               @Param("user1") User user1, 
                                               @Param("user2") User user2);
    
    @Query("SELECT r FROM Room r WHERE r.type = 'GROUP' AND r.name LIKE %:searchTerm%")
    List<Room> searchGroupRooms(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Room r WHERE r.type = :type")
    List<Room> findByType(@Param("type") RoomType type);
} 