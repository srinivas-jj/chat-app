package com.chatapp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatapp.dto.CreateRoomRequest;
import com.chatapp.entity.Message;
import com.chatapp.entity.Room;
import com.chatapp.entity.User;
import com.chatapp.service.ChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomRequest request, 
                                        Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Room room = chatService.createRoom(request, user.getId());
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/rooms/private")
    public ResponseEntity<?> createPrivateRoom(@RequestParam Long userId, 
                                               Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<Room> room = chatService.createOrGetPrivateRoom(currentUser.getId(), userId);
            return ResponseEntity.ok(room.orElse(null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getUserRooms(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Room> rooms = chatService.getUserRooms(user.getId());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<Message>> getRoomMessages(@PathVariable Long roomId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        Page<Message> messages = chatService.getRoomMessages(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/rooms/{roomId}/messages/search")
    public ResponseEntity<List<Message>> searchMessages(@PathVariable Long roomId,
                                                        @RequestParam String query) {
        List<Message> messages = chatService.searchMessagesInRoom(roomId, query);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms/{roomId}/members")
    public ResponseEntity<?> addMemberToRoom(@PathVariable Long roomId,
                                             @RequestParam Long userId) {
        try {
            Room room = chatService.addMemberToRoom(roomId, userId);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/rooms/{roomId}/members/{userId}")
    public ResponseEntity<?> removeMemberFromRoom(@PathVariable Long roomId,
                                                  @PathVariable Long userId) {
        try {
            Room room = chatService.removeMemberFromRoom(roomId, userId);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = chatService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/online")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> users = chatService.getOnlineUsers();
        return ResponseEntity.ok(users);
    }
} 