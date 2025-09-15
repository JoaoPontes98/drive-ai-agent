package com.driveai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull
    private ChatSession session;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private MessageRole role;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;
    
    @Column(name = "file_references", columnDefinition = "JSONB")
    private String fileReferences; // JSON array of Drive file IDs
    
    @Column(columnDefinition = "JSONB")
    private String metadata; // Additional metadata like tokens used, processing time, etc.
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public ChatMessage() {}
    
    public ChatMessage(ChatSession session, MessageRole role, String content) {
        this.session = session;
        this.role = role;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ChatSession getSession() {
        return session;
    }
    
    public void setSession(ChatSession session) {
        this.session = session;
    }
    
    public MessageRole getRole() {
        return role;
    }
    
    public void setRole(MessageRole role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getFileReferences() {
        return fileReferences;
    }
    
    public void setFileReferences(String fileReferences) {
        this.fileReferences = fileReferences;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean isUserMessage() {
        return MessageRole.USER.equals(role);
    }
    
    public boolean isAssistantMessage() {
        return MessageRole.ASSISTANT.equals(role);
    }
    
    public boolean isSystemMessage() {
        return MessageRole.SYSTEM.equals(role);
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", role=" + role +
                ", content='" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    // Enum for message roles
    public enum MessageRole {
        USER, ASSISTANT, SYSTEM
    }
}
