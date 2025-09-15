package com.driveai.repository;

import com.driveai.model.DocumentTemplate;
import com.driveai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {
    
    List<DocumentTemplate> findByUserOrderByUpdatedAtDesc(User user);
    
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.user = :user AND dt.name LIKE %:name% ORDER BY dt.updatedAt DESC")
    List<DocumentTemplate> findByUserAndNameContaining(@Param("user") User user, @Param("name") String name);
    
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.user = :user AND dt.templateContent LIKE %:content% ORDER BY dt.updatedAt DESC")
    List<DocumentTemplate> findByUserAndContentContaining(@Param("user") User user, @Param("content") String content);
    
    Optional<DocumentTemplate> findByIdAndUser(Long id, User user);
    
    void deleteByUser(User user);
}
