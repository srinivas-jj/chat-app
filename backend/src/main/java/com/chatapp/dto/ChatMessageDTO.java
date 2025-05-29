package com.chatapp.dto;

import java.time.LocalDateTime;

import com.chatapp.entity.MessageType;

public class ChatMessageDTO {
    
    private Long id;
    private String content;
    private MessageType type;
    private Long senderId;
    private String senderName;
    private Long roomId;
    private LocalDateTime timestamp;
    private Long num;
    
    // Constructors
    public ChatMessageDTO() {}
    
    public ChatMessageDTO(String content, MessageType type, Long senderId, String senderName, Long roomId) {
        this.content = content;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.roomId = roomId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 