package com.driveai.service;

import com.driveai.dto.DriveFileDto;
import com.driveai.model.DriveFile;
import com.driveai.model.User;
import com.driveai.repository.DriveFileRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleDriveService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);
    
    @Autowired
    private Drive driveService;
    
    @Autowired
    private DriveFileRepository driveFileRepository;
    
    @Autowired
    private GoogleDocsService googleDocsService;
    
    @Autowired
    private GoogleSheetsService googleSheetsService;
    
    @Autowired
    private DocumentProcessingService documentProcessingService;
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    public List<DriveFileDto> listFiles(User user, String query, int maxResults) throws IOException {
        Credential credential = createCredential(user);
        Drive drive = driveService.setHttpRequestInitializer(credential);
        
        Drive.Files.List request = drive.files().list()
                .setQ(query)
                .setPageSize(maxResults)
                .setFields("nextPageToken, files(id, name, mimeType, size, modifiedTime, webViewLink, parents)");
        
        FileList fileList = request.execute();
        List<DriveFileDto> files = new ArrayList<>();
        
        if (fileList.getFiles() != null) {
            for (File file : fileList.getFiles()) {
                DriveFileDto dto = convertToDto(file);
                files.add(dto);
                
                // Cache file metadata
                cacheFileMetadata(user, file);
            }
        }
        
        return files;
    }
    
    public DriveFileDto getFile(User user, String fileId) throws IOException {
        Credential credential = createCredential(user);
        Drive drive = driveService.setHttpRequestInitializer(credential);
        
        File file = drive.files().get(fileId)
                .setFields("id, name, mimeType, size, modifiedTime, webViewLink, parents")
                .execute();
        
        DriveFileDto dto = convertToDto(file);
        cacheFileMetadata(user, file);
        
        return dto;
    }
    
    public String getFileContent(User user, String fileId) throws IOException {
        Credential credential = createCredential(user);
        Drive drive = driveService.setHttpRequestInitializer(credential);
        
        // Get file metadata first
        File file = drive.files().get(fileId).execute();
        String mimeType = file.getMimeType();
        
        // Handle different file types
        if ("application/vnd.google-apps.document".equals(mimeType)) {
            return extractGoogleDocContent(user, fileId);
        } else if ("application/vnd.google-apps.spreadsheet".equals(mimeType)) {
            return extractGoogleSheetContent(user, fileId);
        } else if ("application/pdf".equals(mimeType)) {
            return extractPdfContent(drive, fileId);
        } else if (mimeType != null && mimeType.startsWith("text/")) {
            return extractTextContent(drive, fileId);
        }
        
        return null;
    }
    
    public List<DriveFileDto> searchFiles(User user, String searchQuery, int maxResults) throws IOException {
        String query = String.format("name contains '%s' or fullText contains '%s'", searchQuery, searchQuery);
        return listFiles(user, query, maxResults);
    }
    
    private DriveFileDto convertToDto(File file) {
        DriveFileDto dto = new DriveFileDto();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setMimeType(file.getMimeType());
        dto.setSize(file.getSize());
        dto.setWebViewLink(file.getWebViewLink());
        dto.setFolder("application/vnd.google-apps.folder".equals(file.getMimeType()));
        
        if (file.getModifiedTime() != null) {
            dto.setModifiedTime(LocalDateTime.ofInstant(
                file.getModifiedTime().toInstant(),
                ZoneId.systemDefault()
            ));
        }
        
        if (file.getParents() != null && !file.getParents().isEmpty()) {
            dto.setParentId(file.getParents().get(0));
        }
        
        return dto;
    }
    
    private void cacheFileMetadata(User user, File file) {
        try {
            Optional<DriveFile> existingFile = driveFileRepository.findByIdAndUser(file.getId(), user);
            DriveFile driveFile = existingFile.orElse(new DriveFile());
            
            driveFile.setId(file.getId());
            driveFile.setUser(user);
            driveFile.setName(file.getName());
            driveFile.setMimeType(file.getMimeType());
            driveFile.setSize(file.getSize());
            
            if (file.getModifiedTime() != null) {
                driveFile.setModifiedTime(LocalDateTime.ofInstant(
                    file.getModifiedTime().toInstant(),
                    ZoneId.systemDefault()
                ));
            }
            
            driveFileRepository.save(driveFile);
        } catch (Exception e) {
            logger.warn("Failed to cache file metadata for file {}: {}", file.getId(), e.getMessage());
        }
    }
    
    private Credential createCredential(User user) {
        try {
            return new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setAccessToken(user.getAccessToken())
                    .setRefreshToken(user.getRefreshToken());
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Failed to create Google credential: {}", e.getMessage());
            throw new RuntimeException("Failed to create Google credential", e);
        }
    }
    
    private String extractGoogleDocContent(User user, String fileId) {
        return googleDocsService.extractTextContent(user, fileId);
    }
    
    private String extractGoogleSheetContent(User user, String fileId) {
        return googleSheetsService.extractTextContent(user, fileId);
    }
    
    private String extractPdfContent(Drive drive, String fileId) throws IOException {
        // Download PDF and extract text using PDFBox
        // Implementation would go here
        return null;
    }
    
    private String extractTextContent(Drive drive, String fileId) throws IOException {
        // Download text file content
        // Implementation would go here
        return null;
    }
}
