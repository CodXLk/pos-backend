package com.codX.pos.repository;

import com.codX.pos.entity.PasswordResetOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtpEntity, UUID> {

    Optional<PasswordResetOtpEntity> findByEmailAndOtpAndIsUsedFalseAndExpiryTimeAfter(
            String email, String otp, LocalDateTime currentTime);

    Optional<PasswordResetOtpEntity> findTopByEmailAndIsUsedFalseOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetOtpEntity p SET p.isUsed = true WHERE p.email = :email AND p.isUsed = false")
    void markAllOtpsAsUsedForEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetOtpEntity p WHERE p.expiryTime < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
}
