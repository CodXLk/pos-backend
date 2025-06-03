package com.codX.pos.service.impl;

import com.codX.pos.dto.request.ChangePasswordRequest;
import com.codX.pos.dto.request.SendOtpRequest;
import com.codX.pos.entity.OtpEntity;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.repository.OtpRepository;
import com.codX.pos.repository.UserRepository;
import com.codX.pos.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        // Delete existing OTPs for this phone number and purpose
        otpRepository.deleteByPhoneNumberAndPurpose(request.phoneNumber(), request.purpose());

        String otpCode = generateOtpCode();

        OtpEntity otpEntity = OtpEntity.builder()
                .phoneNumber(request.phoneNumber())
                .otpCode(otpCode)
                .purpose(request.purpose())
                .expiryTime(LocalDateTime.now().plusMinutes(5)) // 5 minutes expiry
                .isUsed(false)
                .createdDate(LocalDateTime.now())
                .build();

        otpRepository.save(otpEntity);

        // Here you would integrate with SMS service to send OTP
        // For now, we'll just log it (remove this in production)
        log.info("OTP for {}: {}", request.phoneNumber(), otpCode);

        // TODO: Integrate with SMS service like AWS SNS, Twilio, etc.
        sendSmsOtp(request.phoneNumber(), otpCode);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // Verify OTP first
        verifyOtp(request.phoneNumber(), request.otpCode(), "PASSWORD_RESET");

        // Find user by phone number
        UserEntity user = userRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found with this phone number"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setDefaultPassword(false);
        userRepository.save(user);

        // Mark OTP as used
        OtpEntity otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsUsedFalseAndExpiryTimeAfter(
                        request.phoneNumber(), request.otpCode(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    public void verifyOtp(String phoneNumber, String otpCode, String purpose) {
        OtpEntity otp = otpRepository.findByPhoneNumberAndOtpCodeAndIsUsedFalseAndExpiryTimeAfter(
                        phoneNumber, otpCode, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        if (!otp.getPurpose().equals(purpose)) {
            throw new RuntimeException("OTP purpose mismatch");
        }
    }

    private String generateOtpCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    private void sendSmsOtp(String phoneNumber, String otpCode) {
        // TODO: Implement SMS sending logic
        // This could be AWS SNS, Twilio, or any other SMS service
        log.info("Sending OTP {} to phone number {}", otpCode, phoneNumber);
    }
}
