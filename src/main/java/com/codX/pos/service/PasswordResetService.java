package com.codX.pos.service;

import com.codX.pos.dto.request.ForgotPasswordRequest;
import com.codX.pos.dto.request.ResetPasswordRequest;
import com.codX.pos.dto.request.VerifyOtpRequest;

public interface PasswordResetService {
    void sendPasswordResetOtp(ForgotPasswordRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
    void cleanupExpiredOtps();
}
