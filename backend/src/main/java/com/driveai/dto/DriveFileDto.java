package com.driveai.dto;

import java.time.LocalDateTime;

public class DriveFileDto {
    
    private String id;
    private String name;
    private String mimeType;
    private Long size;
    private LocalDateTime modifiedTime;
    private String contentSummary;
    private String webViewLink;
    private String downloadLink;
    private boolean isFolder;
    private String parentId;
    
    // Constructors
    public DriveFileDto() {}
    
    public DriveFileDto(String id, String name, String mimeType) {
        this.id = id;
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
    
    public String getWebViewLink() {
        return webViewLink;
    }
    
    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }
    
    public String getDownloadLink() {
        return downloadLink;
    }
    
    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
    
    public boolean isFolder() {
        return isFolder;
    }
    
    public void setFolder(boolean folder) {
        isFolder = folder;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
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
    
    public String getFileType() {
        if (isGoogleDoc()) return "Google Doc";
        if (isGoogleSheet()) return "Google Sheet";
        if (isPdf()) return "PDF";
        if (isTextFile()) return "Text File";
        if (isFolder) return "Folder";
        return "File";
    }
    
    @Override
    public String toString() {
        return "DriveFileDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", modifiedTime=" + modifiedTime +
                ", isFolder=" + isFolder +
                '}';
    }
}
