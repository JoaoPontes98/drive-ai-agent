package com.driveai.service;

import com.driveai.model.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
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
public class GoogleSheetsService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    
    @Autowired
    private Sheets sheetsService;
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    public String extractTextContent(User user, String spreadsheetId) {
        try {
            Credential credential = createCredential(user);
            Sheets sheets = sheetsService.setHttpRequestInitializer(credential);
            
            Spreadsheet spreadsheet = sheets.spreadsheets().get(spreadsheetId).execute();
            return extractTextFromSpreadsheet(sheets, spreadsheet);
            
        } catch (IOException e) {
            logger.error("Error extracting text from Google Sheet {}: {}", spreadsheetId, e.getMessage());
            return null;
        }
    }
    
    public Spreadsheet getSpreadsheet(User user, String spreadsheetId) {
        try {
            Credential credential = createCredential(user);
            Sheets sheets = sheetsService.setHttpRequestInitializer(credential);
            
            return sheets.spreadsheets().get(spreadsheetId).execute();
            
        } catch (IOException e) {
            logger.error("Error getting Google Sheet {}: {}", spreadsheetId, e.getMessage());
            return null;
        }
    }
    
    private String extractTextFromSpreadsheet(Sheets sheets, Spreadsheet spreadsheet) {
        StringBuilder text = new StringBuilder();
        
        if (spreadsheet.getSheets() != null) {
            for (var sheet : spreadsheet.getSheets()) {
                String sheetName = sheet.getProperties().getTitle();
                text.append("Sheet: ").append(sheetName).append("\n");
                
                // Get all data from the sheet
                String range = sheetName + "!A:Z"; // Adjust range as needed
                try {
                    ValueRange response = sheets.spreadsheets().values()
                            .get(spreadsheet.getSpreadsheetId(), range)
                            .execute();
                    
                    List<List<Object>> values = response.getValues();
                    if (values != null) {
                        for (List<Object> row : values) {
                            List<String> rowText = new ArrayList<>();
                            for (Object cell : row) {
                                rowText.add(cell != null ? cell.toString() : "");
                            }
                            text.append(String.join("\t", rowText)).append("\n");
                        }
                    }
                } catch (IOException e) {
                    logger.warn("Error reading sheet {}: {}", sheetName, e.getMessage());
                }
                
                text.append("\n");
            }
        }
        
        return text.toString().trim();
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
