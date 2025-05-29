package com.chatapp.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chatapp.dto.ChatMessageDTO;
import com.chatapp.entity.Message;
import com.chatapp.service.ChatService;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO message, 
                           SimpMessageHeaderAccessor headerAccessor, 
                           Principal principal) {
        try {
            // Save message to database
            Message savedMessage = chatService.saveMessage(message);
            
            // Create response DTO
            ChatMessageDTO responseMessage = new ChatMessageDTO();
            responseMessage.setId(savedMessage.getId());
            responseMessage.setContent(savedMessage.getContent());
            responseMessage.setType(savedMessage.getType());
            responseMessage.setSenderId(savedMessage.getSender().getId());
            responseMessage.setSenderName(savedMessage.getSender().getFullName());
            responseMessage.setRoomId(savedMessage.getRoom().getId());
            responseMessage.setTimestamp(savedMessage.getSentAt());
            
            // Send message to room subscribers
            messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), responseMessage);
            
        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                principal.getName(), 
                "/queue/errors", 
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDTO message,
                       SimpMessageHeaderAccessor headerAccessor,
                       Principal principal) {
        // Add user to WebSocket session
        headerAccessor.getSessionAttributes().put("username", principal.getName());
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());
        
        // Update user online status
        chatService.updateUserOnlineStatus(principal.getName(), true);
        
        // Notify room about user joining
        ChatMessageDTO joinMessage = new ChatMessageDTO();
        joinMessage.setContent(message.getSenderName() + " joined the chat");
        joinMessage.setType(com.chatapp.entity.MessageType.SYSTEM);
        joinMessage.setSenderName("System");
        joinMessage.setRoomId(message.getRoomId());
        
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), joinMessage);
    }

    @MessageMapping("/chat.leaveUser")
    public void leaveUser(@Payload ChatMessageDTO message,
                         SimpMessageHeaderAccessor headerAccessor,
                         Principal principal) {
        // Update user online status
        chatService.updateUserOnlineStatus(principal.getName(), false);
        
        // Notify room about user leaving
        ChatMessageDTO leaveMessage = new ChatMessageDTO();
        leaveMessage.setContent(message.getSenderName() + " left the chat");
        leaveMessage.setType(com.chatapp.entity.MessageType.SYSTEM);
        leaveMessage.setSenderName("System");
        leaveMessage.setRoomId(message.getRoomId());
        
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), leaveMessage);
    }

    @MessageMapping("/chat.typing")
    public void userTyping(@Payload ChatMessageDTO message) {
        // Broadcast typing indicator to room (excluding sender)
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/typing", message);
    }
} 