package com.driveai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatRequest {
    
    @NotBlank(message = "Message content is required")
    private String message;
    
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    
    private String context; // Additional context for the AI
    private String[] fileIds; // Drive file IDs to include in context
    
    // Constructors
    public ChatRequest() {}
    
    public ChatRequest(String message, Long sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public String[] getFileIds() {
        return fileIds;
    }
    
    public void setFileIds(String[] fileIds) {
        this.fileIds = fileIds;
    }
    
    @Override
    public String toString() {
        return "ChatRequest{" +
                "message='" + message + '\'' +
                ", sessionId=" + sessionId +
                ", context='" + context + '\'' +
                '}';
    }
}
