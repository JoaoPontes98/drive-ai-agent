package com.driveai.controller;

import com.driveai.dto.ChatRequest;
import com.driveai.dto.ChatResponse;
import com.driveai.model.ChatMessage;
import com.driveai.model.ChatSession;
import com.driveai.model.User;
import com.driveai.repository.ChatMessageRepository;
import com.driveai.repository.ChatSessionRepository;
import com.driveai.repository.UserRepository;
import com.driveai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AiAgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AiAgentController.class);
    
    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private ChatSessionRepository chatSessionRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody ChatRequest request) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            // Get or create chat session
            ChatSession session = getOrCreateSession(user, request.getSessionId());
            
            // Save user message
            ChatMessage userMessage = new ChatMessage(session, ChatMessage.MessageRole.USER, request.getMessage());
            userMessage = chatMessageRepository.save(userMessage);
            
            // Get conversation history
            List<ChatMessage> conversationHistory = chatMessageRepository.findBySessionOrderByCreatedAtAsc(session);
            
            // Generate AI response
            String aiResponse = openAiService.generateResponse(conversationHistory, request.getContext());
            
            // Save AI response
            ChatMessage aiMessage = new ChatMessage(session, ChatMessage.MessageRole.ASSISTANT, aiResponse);
            aiMessage = chatMessageRepository.save(aiMessage);
            
            // Update session timestamp
            session.setUpdatedAt(LocalDateTime.now());
            chatSessionRepository.save(session);
            
            ChatResponse response = new ChatResponse(
                aiMessage.getId(),
                aiResponse,
                "assistant",
                aiMessage.getCreatedAt()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process message: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<?> getChatSessions(@AuthenticationPrincipal OAuth2User principal) {
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            List<ChatSession> sessions = chatSessionRepository.findByUserOrderByUpdatedAtDesc(user);
            
            return ResponseEntity.ok(Map.of(
                "sessions", sessions,
                "count", sessions.size()
            ));
            
        } catch (Exception e) {
            logger.error("Error getting chat sessions: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get chat sessions"));
        }
    }
    
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<?> getSessionMessages(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long sessionId) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty() || !sessionOpt.get().getUser().equals(user)) {
                return ResponseEntity.status(404).body(Map.of("error", "Session not found"));
            }
            
            List<ChatMessage> messages = chatMessageRepository.findBySessionOrderByCreatedAtAsc(sessionOpt.get());
            
            return ResponseEntity.ok(Map.of(
                "messages", messages,
                "count", messages.size()
            ));
            
        } catch (Exception e) {
            logger.error("Error getting session messages: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get session messages"));
        }
    }
    
    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(required = false) String title) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String sessionTitle = title != null ? title : "New Chat Session";
            ChatSession session = new ChatSession(user, sessionTitle);
            session = chatSessionRepository.save(session);
            
            return ResponseEntity.ok(session);
            
        } catch (Exception e) {
            logger.error("Error creating chat session: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create chat session"));
        }
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<?> deleteSession(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long sessionId) {
        
        try {
            User user = getCurrentUser(principal);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty() || !sessionOpt.get().getUser().equals(user)) {
                return ResponseEntity.status(404).body(Map.of("error", "Session not found"));
            }
            
            chatSessionRepository.delete(sessionOpt.get());
            
            return ResponseEntity.ok(Map.of("message", "Session deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting chat session: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete chat session"));
        }
    }
    
    private User getCurrentUser(OAuth2User principal) {
        if (principal == null) {
            return null;
        }
        
        String googleId = principal.getAttribute("id");
        return userRepository.findByGoogleId(googleId).orElse(null);
    }
    
    private ChatSession getOrCreateSession(User user, Long sessionId) {
        if (sessionId != null) {
            Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
            if (sessionOpt.isPresent() && sessionOpt.get().getUser().equals(user)) {
                return sessionOpt.get();
            }
        }
        
        // Create new session
        ChatSession newSession = new ChatSession(user, "New Chat Session");
        return chatSessionRepository.save(newSession);
    }
}
