package com.driveai.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {
    
    private Long messageId;
    private String content;
    private String role;
    private LocalDateTime timestamp;
    private List<String> fileReferences;
    private Object metadata;
    private boolean streaming;
    
    // Constructors
    public ChatResponse() {}
    
    public ChatResponse(String content, String role) {
        this.content = content;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatResponse(Long messageId, String content, String role, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.content = content;
        this.role = role;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getFileReferences() {
        return fileReferences;
    }
    
    public void setFileReferences(List<String> fileReferences) {
        this.fileReferences = fileReferences;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    public boolean isStreaming() {
        return streaming;
    }
    
    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }
    
    @Override
    public String toString() {
        return "ChatResponse{" +
                "messageId=" + messageId +
                ", content='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", role='" + role + '\'' +
                ", timestamp=" + timestamp +
                ", streaming=" + streaming +
                '}';
    }
}
