package com.chatapp.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatapp.dto.ChatMessageDTO;
import com.chatapp.dto.CreateRoomRequest;
import com.chatapp.entity.Message;
import com.chatapp.entity.Room;
import com.chatapp.entity.RoomType;
import com.chatapp.entity.User;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.RoomRepository;
import com.chatapp.repository.UserRepository;

@Service
public class ChatService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Room createRoom(CreateRoomRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Room room = new Room();
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setType(request.getType());
        room.setCreatedBy(creator);

        // Add creator as a member
        room.addMember(creator);

        // Add other members if specified
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            Set<User> members = userRepository.findAllById(request.getMemberIds())
                    .stream().collect(Collectors.toSet());
            members.forEach(room::addMember);
        }

        return roomRepository.save(room);
    }

    public Optional<Room> createOrGetPrivateRoom(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User2 not found"));

        // Check if private room already exists between these users
        Optional<Room> existingRoom = roomRepository.findPrivateRoomBetweenUsers(
                RoomType.PRIVATE, user1, user2);

        if (existingRoom.isPresent()) {
            return existingRoom;
        }

        // Create new private room
        Room room = new Room();
        room.setName(user1.getFullName() + " & " + user2.getFullName());
        room.setType(RoomType.PRIVATE);
        room.setCreatedBy(user1);
        room.addMember(user1);
        room.addMember(user2);

        return Optional.of(roomRepository.save(room));
    }

    public List<Room> getUserRooms(Long userId) {
        return roomRepository.findRoomsByUserId(userId);
    }

    @Transactional
    public Message saveMessage(ChatMessageDTO messageDTO) {
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Room room = roomRepository.findById(messageDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Verify sender is a member of the room
        if (!room.isMember(sender)) {
            throw new RuntimeException("User is not a member of this room");
        }

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setType(messageDTO.getType());
        message.setSender(sender);
        message.setRoom(room);

        return messageRepository.save(message);
    }

    public Page<Message> getRoomMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByRoomIdOrderBySentAtDesc(roomId, pageable);
    }

    public List<Message> searchMessagesInRoom(Long roomId, String searchTerm) {
        return messageRepository.searchMessagesInRoom(roomId, searchTerm);
    }

    @Transactional
    public Room addMemberToRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (room.getType() == RoomType.PRIVATE) {
            throw new RuntimeException("Cannot add members to private room");
        }

        room.addMember(user);
        return roomRepository.save(room);
    }

    @Transactional
    public Room removeMemberFromRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        room.removeMember(user);
        return roomRepository.save(room);
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    public List<User> getOnlineUsers() {
        return userRepository.findOnlineUsers();
    }

    @Transactional
    public void updateUserOnlineStatus(String email, boolean isOnline) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setOnline(isOnline);
        userRepository.save(user);
    }
} 