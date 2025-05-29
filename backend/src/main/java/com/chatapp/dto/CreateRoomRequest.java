package com.chatapp.dto;

import java.util.Set;

import com.chatapp.entity.RoomType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRoomRequest {
    
    @NotBlank(message = "Room name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Room type is required")
    private RoomType type;
    
    private Set<Long> memberIds;
    
    // Constructors
    public CreateRoomRequest() {}
    
    public CreateRoomRequest(String name, String description, RoomType type, Set<Long> memberIds) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.memberIds = memberIds;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RoomType getType() {
        return type;
    }
    
    public void setType(RoomType type) {
        this.type = type;
    }
    
    public Set<Long> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
} 