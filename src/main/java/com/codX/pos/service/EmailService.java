package com.codX.pos.service;

public interface EmailService {
    void sendPasswordResetOtp(String email, String otp);
    void sendWelcomeEmail(String email, String firstName, String temporaryPassword);
}
