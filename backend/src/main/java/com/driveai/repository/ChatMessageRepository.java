package com.driveai.repository;

import com.driveai.model.ChatMessage;
import com.driveai.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session = :session ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesBySession(@Param("session") ChatSession session);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session = :session AND cm.role = :role ORDER BY cm.createdAt ASC")
    List<ChatMessage> findBySessionAndRole(@Param("session") ChatSession session, @Param("role") ChatMessage.MessageRole role);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.session = :session")
    long countBySession(@Param("session") ChatSession session);
    
    void deleteBySession(ChatSession session);
}
