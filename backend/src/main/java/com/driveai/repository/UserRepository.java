package com.driveai.repository;

import com.driveai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByGoogleId(String googleId);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.googleId = :googleId AND u.tokenExpiresAt > CURRENT_TIMESTAMP")
    Optional<User> findByGoogleIdWithValidToken(@Param("googleId") String googleId);
    
    boolean existsByGoogleId(String googleId);
    
    boolean existsByEmail(String email);
}
