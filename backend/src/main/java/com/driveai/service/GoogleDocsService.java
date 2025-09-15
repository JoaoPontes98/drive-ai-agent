package com.driveai.service;

import com.driveai.model.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleDocsService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleDocsService.class);
    
    @Autowired
    private Docs docsService;
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    public String extractTextContent(User user, String documentId) {
        try {
            Credential credential = createCredential(user);
            Docs docs = docsService.setHttpRequestInitializer(credential);
            
            Document document = docs.documents().get(documentId).execute();
            return extractTextFromDocument(document);
            
        } catch (IOException e) {
            logger.error("Error extracting text from Google Doc {}: {}", documentId, e.getMessage());
            return null;
        }
    }
    
    public Document getDocument(User user, String documentId) {
        try {
            Credential credential = createCredential(user);
            Docs docs = docsService.setHttpRequestInitializer(credential);
            
            return docs.documents().get(documentId).execute();
            
        } catch (IOException e) {
            logger.error("Error getting Google Doc {}: {}", documentId, e.getMessage());
            return null;
        }
    }
    
    private String extractTextFromDocument(Document document) {
        StringBuilder text = new StringBuilder();
        
        if (document.getBody() != null && document.getBody().getContent() != null) {
            for (var element : document.getBody().getContent()) {
                if (element.getParagraph() != null) {
                    extractTextFromParagraph(element.getParagraph(), text);
                }
            }
        }
        
        return text.toString().trim();
    }
    
    private void extractTextFromParagraph(com.google.api.services.docs.v1.model.Paragraph paragraph, StringBuilder text) {
        if (paragraph.getElements() != null) {
            for (var element : paragraph.getElements()) {
                if (element.getTextRun() != null && element.getTextRun().getContent() != null) {
                    text.append(element.getTextRun().getContent());
                }
            }
        }
        text.append("\n");
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
}
