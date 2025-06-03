package com.codX.pos.controller;

import com.codX.pos.dto.request.ChangePasswordRequest;
import com.codX.pos.dto.request.SendOtpRequest;
import com.codX.pos.service.OtpService;
import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otp")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "OTP Management", description = "OTP-based authentication and password management")
public class OTPController {

    private final OtpService otpService;

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP to phone number",
            description = "Send a one-time password (OTP) to the specified phone number for verification purposes. The OTP expires in 5 minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number or purpose"),
            @ApiResponse(responseCode = "500", description = "Failed to send OTP")
    })
    public ResponseEntity<?> sendOtp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "OTP request details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SendOtpRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "phoneNumber": "+1234567890",
                            "purpose": "PASSWORD_RESET"
                        }
                        """)
                    )
            )
            @Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtp(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "OTP sent successfully"),
                HttpStatus.OK
        );
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change password using OTP",
            description = "Change user password using OTP verification. The user must provide a valid OTP received on their phone number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "404", description = "User not found with this phone number")
    })
    public ResponseEntity<?> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password change request with OTP verification",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChangePasswordRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "phoneNumber": "+1234567890",
                            "otpCode": "123456",
                            "newPassword": "newSecurePassword123"
                        }
                        """)
                    )
            )
            @Valid @RequestBody ChangePasswordRequest request) {
        otpService.changePassword(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Password changed successfully"),
                HttpStatus.OK
        );
    }
}
