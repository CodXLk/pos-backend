package com.codX.pos.repository;

import com.codX.pos.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {
    Optional<OtpEntity> findByPhoneNumberAndOtpCodeAndIsUsedFalseAndExpiryTimeAfter(
            String phoneNumber, String otpCode, LocalDateTime currentTime);

    void deleteByPhoneNumberAndPurpose(String phoneNumber, String purpose);
    void deleteByExpiryTimeBefore(LocalDateTime currentTime);
}
