package com.driveai.service;

import com.driveai.config.OpenAiConfig;
import com.driveai.model.ChatMessage;
import com.driveai.model.DriveFile;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage as OpenAiChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService as OpenAiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);
    
    @Autowired
    private OpenAiClient openAiClient;
    
    @Autowired
    private OpenAiConfig openAiConfig;
    
    public String generateResponse(List<ChatMessage> conversationHistory, String context) {
        try {
            List<OpenAiChatMessage> messages = buildMessageList(conversationHistory, context);
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiConfig.getModel())
                    .messages(messages)
                    .maxTokens(openAiConfig.getMaxTokens())
                    .temperature(openAiConfig.getTemperature())
                    .build();
            
            ChatCompletionResult result = openAiClient.createChatCompletion(request);
            
            if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                return result.getChoices().get(0).getMessage().getContent();
            }
            
            return "I'm sorry, I couldn't generate a response at this time.";
            
        } catch (Exception e) {
            logger.error("Error generating AI response: {}", e.getMessage());
            return "I'm sorry, there was an error processing your request.";
        }
    }
    
    public String analyzeDocument(DriveFile file, String content) {
        try {
            String prompt = buildDocumentAnalysisPrompt(file, content);
            
            List<OpenAiChatMessage> messages = List.of(
                OpenAiChatMessage.builder()
                    .role("system")
                    .content("You are an AI assistant that analyzes documents and provides concise summaries and key insights.")
                    .build(),
                OpenAiChatMessage.builder()
                    .role("user")
                    .content(prompt)
                    .build()
            );
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiConfig.getModel())
                    .messages(messages)
                    .maxTokens(1000)
                    .temperature(0.3)
                    .build();
            
            ChatCompletionResult result = openAiClient.createChatCompletion(request);
            
            if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                return result.getChoices().get(0).getMessage().getContent();
            }
            
            return "Unable to analyze document content.";
            
        } catch (Exception e) {
            logger.error("Error analyzing document: {}", e.getMessage());
            return "Error analyzing document content.";
        }
    }
    
    public String generateDocumentSummary(List<DriveFile> files) {
        try {
            String prompt = buildDocumentSummaryPrompt(files);
            
            List<OpenAiChatMessage> messages = List.of(
                OpenAiChatMessage.builder()
                    .role("system")
                    .content("You are an AI assistant that creates comprehensive summaries of multiple documents.")
                    .build(),
                OpenAiChatMessage.builder()
                    .role("user")
                    .content(prompt)
                    .build()
            );
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiConfig.getModel())
                    .messages(messages)
                    .maxTokens(1500)
                    .temperature(0.4)
                    .build();
            
            ChatCompletionResult result = openAiClient.createChatCompletion(request);
            
            if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                return result.getChoices().get(0).getMessage().getContent();
            }
            
            return "Unable to generate document summary.";
            
        } catch (Exception e) {
            logger.error("Error generating document summary: {}", e.getMessage());
            return "Error generating document summary.";
        }
    }
    
    private List<OpenAiChatMessage> buildMessageList(List<ChatMessage> conversationHistory, String context) {
        List<OpenAiChatMessage> messages = new ArrayList<>();
        
        // Add system message with context
        String systemMessage = "You are an AI assistant that helps users manage and analyze their Google Drive files. " +
                "You can help with file organization, content analysis, document creation, and business process automation. " +
                "Always be helpful and provide accurate information based on the user's Drive content.";
        
        if (context != null && !context.isEmpty()) {
            systemMessage += "\n\nContext: " + context;
        }
        
        messages.add(OpenAiChatMessage.builder()
                .role("system")
                .content(systemMessage)
                .build());
        
        // Add conversation history
        for (ChatMessage message : conversationHistory) {
            messages.add(OpenAiChatMessage.builder()
                    .role(message.getRole().name().toLowerCase())
                    .content(message.getContent())
                    .build());
        }
        
        return messages;
    }
    
    private String buildDocumentAnalysisPrompt(DriveFile file, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please analyze the following document and provide:\n");
        prompt.append("1. A brief summary (2-3 sentences)\n");
        prompt.append("2. Key topics and themes\n");
        prompt.append("3. Important dates, names, or numbers mentioned\n");
        prompt.append("4. Suggested tags or categories\n\n");
        prompt.append("Document: ").append(file.getName()).append("\n");
        prompt.append("Type: ").append(file.getMimeType()).append("\n");
        prompt.append("Content:\n").append(content);
        
        return prompt.toString();
    }
    
    private String buildDocumentSummaryPrompt(List<DriveFile> files) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please create a comprehensive summary of the following documents:\n\n");
        
        for (DriveFile file : files) {
            prompt.append("- ").append(file.getName()).append(" (").append(file.getMimeType()).append(")\n");
            if (file.getContentSummary() != null) {
                prompt.append("  Summary: ").append(file.getContentSummary()).append("\n");
            }
        }
        
        prompt.append("\nPlease provide:\n");
        prompt.append("1. Overall theme and purpose of these documents\n");
        prompt.append("2. Common topics and patterns\n");
        prompt.append("3. Key insights and findings\n");
        prompt.append("4. Recommendations for organization or next steps");
        
        return prompt.toString();
    }
}
