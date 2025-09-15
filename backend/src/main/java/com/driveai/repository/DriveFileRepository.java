package com.driveai.repository;

import com.driveai.model.DriveFile;
import com.driveai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFileRepository extends JpaRepository<DriveFile, String> {
    
    List<DriveFile> findByUserOrderByModifiedTimeDesc(User user);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.name LIKE %:name% ORDER BY df.modifiedTime DESC")
    List<DriveFile> findByUserAndNameContaining(@Param("user") User user, @Param("name") String name);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.mimeType = :mimeType ORDER BY df.modifiedTime DESC")
    List<DriveFile> findByUserAndMimeType(@Param("user") User user, @Param("mimeType") String mimeType);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.contentSummary IS NOT NULL ORDER BY df.modifiedTime DESC")
    List<DriveFile> findAnalyzedFilesByUser(@Param("user") User user);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND (df.contentSummary IS NULL OR df.lastAnalyzed < :threshold) ORDER BY df.modifiedTime DESC")
    List<DriveFile> findFilesNeedingAnalysis(@Param("user") User user, @Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.contentText LIKE %:searchTerm% ORDER BY df.modifiedTime DESC")
    List<DriveFile> searchByContent(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    Optional<DriveFile> findByIdAndUser(String id, User user);
    
    void deleteByUser(User user);
}
