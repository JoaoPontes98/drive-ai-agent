package com.driveai.controller;

import com.driveai.dto.DriveFileDto;
import com.driveai.model.User;
import com.driveai.repository.UserRepository;
import com.driveai.service.GoogleDriveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/drive")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class DriveController {
    
    private static final Logger logger = LoggerFactory.getLogger(DriveController.class);
    
    @Autowired
    private GoogleDriveService googleDriveService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/files")
    public ResponseEntity<?> listFiles(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(defaultValue = "10") int maxResults,
            @RequestParam(required = false) String query) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String searchQuery = query != null ? query : "trashed=false";
            List<DriveFileDto> files = googleDriveService.listFiles(user, searchQuery, maxResults);
            
            return ResponseEntity.ok(Map.of(
                "files", files,
                "count", files.size(),
                "query", searchQuery
            ));
            
        } catch (IOException e) {
            logger.error("Error listing files: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to list files: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> getFile(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable String fileId) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            DriveFileDto file = googleDriveService.getFile(user, fileId);
            return ResponseEntity.ok(file);
            
        } catch (IOException e) {
            logger.error("Error getting file {}: {}", fileId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get file: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/files/{fileId}/content")
    public ResponseEntity<?> getFileContent(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable String fileId) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String content = googleDriveService.getFileContent(user, fileId);
            
            if (content == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Content not available for this file type"));
            }
            
            return ResponseEntity.ok(Map.of(
                "fileId", fileId,
                "content", content
            ));
            
        } catch (IOException e) {
            logger.error("Error getting file content {}: {}", fileId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get file content: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            List<DriveFileDto> files = googleDriveService.searchFiles(user, q, maxResults);
            
            return ResponseEntity.ok(Map.of(
                "files", files,
                "count", files.size(),
                "query", q
            ));
            
        } catch (IOException e) {
            logger.error("Error searching files: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to search files: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/folders")
    public ResponseEntity<?> listFolders(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(required = false) String parentId,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String query = "mimeType='application/vnd.google-apps.folder' and trashed=false";
            if (parentId != null) {
                query += " and '" + parentId + "' in parents";
            }
            
            List<DriveFileDto> folders = googleDriveService.listFiles(user, query, maxResults);
            
            return ResponseEntity.ok(Map.of(
                "folders", folders,
                "count", folders.size(),
                "parentId", parentId
            ));
            
        } catch (IOException e) {
            logger.error("Error listing folders: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to list folders: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    private User getCurrentUser(OAuth2User principal) {
        if (principal == null) {
            return null;
        }
        
        String googleId = principal.getAttribute("id");
        return userRepository.findByGoogleId(googleId).orElse(null);
    }
}
