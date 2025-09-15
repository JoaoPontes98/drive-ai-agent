package com.driveai.service;

import com.driveai.model.DriveFile;
import com.driveai.model.User;
import com.driveai.repository.DriveFileRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);
    
    @Autowired
    private GoogleDriveService googleDriveService;
    
    @Autowired
    private GoogleDocsService googleDocsService;
    
    @Autowired
    private GoogleSheetsService googleSheetsService;
    
    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private DriveFileRepository driveFileRepository;
    
    public String extractTextContent(User user, DriveFile file) {
        try {
            String content = null;
            
            if (file.isGoogleDoc()) {
                content = googleDocsService.extractTextContent(user, file.getId());
            } else if (file.isGoogleSheet()) {
                content = googleSheetsService.extractTextContent(user, file.getId());
            } else if (file.isPdf()) {
                content = extractPdfText(user, file.getId());
            } else if (file.isTextFile()) {
                content = extractTextFileContent(user, file.getId());
            }
            
            // Update the file with extracted content
            if (content != null && !content.isEmpty()) {
                file.setContentText(content);
                file.setLastAnalyzed(LocalDateTime.now());
                driveFileRepository.save(file);
            }
            
            return content;
            
        } catch (Exception e) {
            logger.error("Error extracting text content from file {}: {}", file.getId(), e.getMessage());
            return null;
        }
    }
    
    public String analyzeDocument(User user, DriveFile file) {
        try {
            String content = file.getContentText();
            if (content == null || content.isEmpty()) {
                content = extractTextContent(user, file);
            }
            
            if (content == null || content.isEmpty()) {
                return "Unable to extract content for analysis.";
            }
            
            String analysis = openAiService.analyzeDocument(file, content);
            
            // Update the file with analysis
            file.setContentSummary(analysis);
            file.setLastAnalyzed(LocalDateTime.now());
            driveFileRepository.save(file);
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error analyzing document {}: {}", file.getId(), e.getMessage());
            return "Error analyzing document content.";
        }
    }
    
    public void processFilesNeedingAnalysis(User user) {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(1);
            List<DriveFile> filesToProcess = driveFileRepository.findFilesNeedingAnalysis(user, threshold);
            
            logger.info("Processing {} files for analysis", filesToProcess.size());
            
            for (DriveFile file : filesToProcess) {
                try {
                    analyzeDocument(user, file);
                    Thread.sleep(1000); // Rate limiting
                } catch (Exception e) {
                    logger.warn("Failed to analyze file {}: {}", file.getId(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error processing files for analysis: {}", e.getMessage());
        }
    }
    
    private String extractPdfText(User user, String fileId) {
        try {
            // This would require downloading the PDF file first
            // For now, return a placeholder
            logger.info("PDF text extraction not yet implemented for file {}", fileId);
            return null;
        } catch (Exception e) {
            logger.error("Error extracting PDF text from file {}: {}", fileId, e.getMessage());
            return null;
        }
    }
    
    private String extractTextFileContent(User user, String fileId) {
        try {
            // This would require downloading the text file first
            // For now, return a placeholder
            logger.info("Text file content extraction not yet implemented for file {}", fileId);
            return null;
        } catch (Exception e) {
            logger.error("Error extracting text file content from file {}: {}", fileId, e.getMessage());
            return null;
        }
    }
    
    public String extractPdfTextFromBytes(byte[] pdfBytes) {
        try (InputStream inputStream = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(inputStream)) {
            
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
            
        } catch (IOException e) {
            logger.error("Error extracting text from PDF: {}", e.getMessage());
            return null;
        }
    }
    
    public String extractTextFromBytes(byte[] textBytes, String charset) {
        try {
            return new String(textBytes, charset != null ? charset : StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("Error extracting text from bytes: {}", e.getMessage());
            return null;
        }
    }
}
