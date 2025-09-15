package com.driveai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "drive_files")
public class DriveFile {
    
    @Id
    @Column(name = "id")
    @NotBlank
    private String id; // Google Drive file ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @NotBlank
    private String name;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column
    private Long size;
    
    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;
    
    @Column(name = "content_summary", columnDefinition = "TEXT")
    private String contentSummary;
    
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;
    
    @Column(name = "last_analyzed")
    private LocalDateTime lastAnalyzed;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public DriveFile() {}
    
    public DriveFile(String id, User user, String name, String mimeType) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.mimeType = mimeType;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }
    
    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    
    public String getContentSummary() {
        return contentSummary;
    }
    
    public void setContentSummary(String contentSummary) {
        this.contentSummary = contentSummary;
    }
    
    public String getContentText() {
        return contentText;
    }
    
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
    
    public LocalDateTime getLastAnalyzed() {
        return lastAnalyzed;
    }
    
    public void setLastAnalyzed(LocalDateTime lastAnalyzed) {
        this.lastAnalyzed = lastAnalyzed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean isGoogleDoc() {
        return "application/vnd.google-apps.document".equals(mimeType);
    }
    
    public boolean isGoogleSheet() {
        return "application/vnd.google-apps.spreadsheet".equals(mimeType);
    }
    
    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }
    
    public boolean isTextFile() {
        return mimeType != null && mimeType.startsWith("text/");
    }
    
    public boolean needsAnalysis() {
        return contentSummary == null || 
               lastAnalyzed == null || 
               LocalDateTime.now().minusDays(1).isAfter(lastAnalyzed);
    }
    
    @Override
    public String toString() {
        return "DriveFile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
