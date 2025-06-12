package com.codX.pos.config;

import com.codX.pos.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final PasswordResetService passwordResetService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredOtps() {
        try {
            passwordResetService.cleanupExpiredOtps();
        } catch (Exception e) {
            log.error("Failed to cleanup expired OTPs", e);
        }
    }
}
