package com.driveai.repository;

import com.driveai.model.ChatSession;
import com.driveai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    
    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);
    
    @Query("SELECT cs FROM ChatSession cs WHERE cs.user = :user ORDER BY cs.updatedAt DESC")
    List<ChatSession> findRecentSessionsByUser(@Param("user") User user);
    
    @Query("SELECT cs FROM ChatSession cs WHERE cs.user = :user AND cs.title LIKE %:title% ORDER BY cs.updatedAt DESC")
    List<ChatSession> findByUserAndTitleContaining(@Param("user") User user, @Param("title") String title);
    
    void deleteByUser(User user);
}
