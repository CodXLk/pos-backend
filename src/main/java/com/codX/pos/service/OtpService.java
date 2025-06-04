package com.codX.pos.service;

import com.codX.pos.dto.request.ChangePasswordRequest;
import com.codX.pos.dto.request.SendOtpRequest;

public interface OtpService {
    void sendOtp(SendOtpRequest request);
    void changePassword(ChangePasswordRequest request);
    void verifyOtp(String phoneNumber, String otpCode, String purpose);
}
