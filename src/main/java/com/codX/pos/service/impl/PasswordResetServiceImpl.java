package com.codX.pos.service.impl;

import com.codX.pos.dto.request.ForgotPasswordRequest;
import com.codX.pos.dto.request.ResetPasswordRequest;
import com.codX.pos.dto.request.VerifyOtpRequest;
import com.codX.pos.entity.PasswordResetOtpEntity;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.repository.PasswordResetOtpRepository;
import com.codX.pos.repository.UserRepository;
import com.codX.pos.service.EmailService;
import com.codX.pos.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${otp.expiration-minutes:10}")
    private int otpExpirationMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void sendPasswordResetOtp(ForgotPasswordRequest request) {
        // Check if user exists
        Optional<UserEntity> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isEmpty()) {
            // For security reasons, don't reveal if email exists or not
            log.warn("Password reset attempted for non-existent email: {}", request.email());
            return; // Don't throw exception, just return silently
        }

        UserEntity user = userOptional.get();
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Mark all previous OTPs as used
        otpRepository.markAllOtpsAsUsedForEmail(request.email());

        // Generate new OTP
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        // Save OTP
        PasswordResetOtpEntity otpEntity = PasswordResetOtpEntity.builder()
                .email(request.email())
                .otp(otp)
                .expiryTime(expiryTime)
                .isUsed(false)
                .build();

        otpRepository.save(otpEntity);

        // Send email
        try {
            emailService.sendPasswordResetOtp(request.email(), otp);
            log.info("Password reset OTP sent to email: {}", request.email());
        } catch (Exception e) {
            log.error("Failed to send password reset OTP to email: {}", request.email(), e);
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        Optional<PasswordResetOtpEntity> otpOptional = otpRepository
                .findByEmailAndOtpAndIsUsedFalseAndExpiryTimeAfter(
                        request.email(), request.otp(), LocalDateTime.now());

        return otpOptional.isPresent();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Verify OTP first
        Optional<PasswordResetOtpEntity> otpOptional = otpRepository
                .findByEmailAndOtpAndIsUsedFalseAndExpiryTimeAfter(
                        request.email(), request.otp(), LocalDateTime.now());

        if (otpOptional.isEmpty()) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Find user
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setDefaultPassword(false);
        userRepository.save(user);

        // Mark OTP as used
        PasswordResetOtpEntity otpEntity = otpOptional.get();
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        log.info("Password reset successfully for email: {}", request.email());
    }

    @Override
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        log.info("Cleaned up expired OTPs");
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
