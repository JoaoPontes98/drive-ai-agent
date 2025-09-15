package com.driveai.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class GoogleApiConfig {
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Drive AI Agent";
    
    // Define the scopes required for the application
    private static final List<String> SCOPES = Arrays.asList(
        DriveScopes.DRIVE_READONLY,
        DriveScopes.DRIVE_FILE,
        DocsScopes.DOCUMENTS,
        DocsScopes.DOCUMENTS_READONLY,
        SheetsScopes.SPREADSHEETS,
        SheetsScopes.SPREADSHEETS_READONLY
    );
    
    @Bean
    public NetHttpTransport httpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }
    
    @Bean
    public GoogleClientSecrets googleClientSecrets() throws IOException {
        // In a real application, you would load this from a secure configuration
        // For now, we'll create it programmatically
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(details);
        
        return clientSecrets;
    }
    
    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(
            NetHttpTransport httpTransport, 
            GoogleClientSecrets clientSecrets) throws IOException {
        
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }
    
    @Bean
    public Drive driveService(NetHttpTransport httpTransport) {
        return new Drive.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    @Bean
    public Docs docsService(NetHttpTransport httpTransport) {
        return new Docs.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    @Bean
    public Sheets sheetsService(NetHttpTransport httpTransport) {
        return new Sheets.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
