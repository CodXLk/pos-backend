package com.codX.pos.controller;

import com.codX.pos.dto.request.ChangePasswordRequest;
import com.codX.pos.dto.request.SendOtpRequest;
import com.codX.pos.service.OtpService;
import com.codX.pos.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtp(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "OTP sent successfully"),
                HttpStatus.OK
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        otpService.changePassword(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Password changed successfully"),
                HttpStatus.OK
        );
    }
}
