package com.codX.pos.auth;

import com.codX.pos.dto.request.ChangePasswordRequest;
import com.codX.pos.dto.request.ForgotPasswordRequest;
import com.codX.pos.dto.request.ResetPasswordRequest;
import com.codX.pos.dto.request.VerifyOtpRequest;
import com.codX.pos.service.PasswordResetService;
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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication", description = "Authentication and authorization management APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Register a new user with specified role. This is a basic registration endpoint that creates a user and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "code": 201,
                        "data": {
                            "token": "eyJhbGciOiJIUzI1NiJ9..."
                        },
                        "message": "User Created Successfully"
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Username already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<StandardResponse> register(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "userName": "johndoe",
                            "password": "password123",
                            "email": "john.doe@example.com",
                            "role": "COMPANY_ADMIN"
                        }
                        """)
                    )
            )
            @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(
                new StandardResponse(201, authenticationService.register(request), "User Created Successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Authenticate user",
            description = "Login with username and password to receive JWT token for API access. The token should be used in Authorization header as 'Bearer {token}'"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "data": {
                            "token": "eyJhbGciOiJIUzI1NiJ9..."
                        },
                        "message": "User logged Successfully"
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account deactivated")
    })
    public ResponseEntity<StandardResponse> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "userName": "johndoe",
                            "password": "password123"
                        }
                        """)
                    )
            )
            @RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(
                new StandardResponse(200, authenticationService.authenticate(authenticationRequest), "User logged Successfully"),
                HttpStatus.OK
        );
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Send password reset OTP",
            description = "Send a 6-digit OTP to the user's email for password reset. The OTP expires in 10 minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    public ResponseEntity<StandardResponse> forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email address for password reset",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ForgotPasswordRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "email": "user@aurionx.lk"
                        }
                        """)
                    )
            )
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendPasswordResetOtp(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Password reset OTP sent to your email"),
                HttpStatus.OK
        );
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify password reset OTP",
            description = "Verify the 6-digit OTP sent to the user's email. This step is optional but recommended for better UX."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    public ResponseEntity<StandardResponse> verifyOtp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email and OTP for verification",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VerifyOtpRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "email": "user@aurionx.lk",
                            "otp": "123456"
                        }
                        """)
                    )
            )
            @Valid @RequestBody VerifyOtpRequest request) {
        boolean isValid = passwordResetService.verifyOtp(request);
        if (isValid) {
            return new ResponseEntity<>(
                    new StandardResponse(200, null, "OTP verified successfully"),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    new StandardResponse(400, null, "Invalid or expired OTP"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password with OTP",
            description = "Reset the user's password using the verified OTP and new password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<StandardResponse> resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email, OTP, and new password",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ResetPasswordRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "email": "user@aurionx.lk",
                            "otp": "123456",
                            "newPassword": "newSecurePassword123"
                        }
                        """)
                    )
            )
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Password reset successfully"),
                HttpStatus.OK
        );
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change password for authenticated user",
            description = "Change password for the currently authenticated user. Requires current password verification."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<StandardResponse> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Current and new password",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChangePasswordRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "currentPassword": "oldPassword123",
                            "newPassword": "newSecurePassword123"
                        }
                        """)
                    )
            )
            @Valid @RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Password changed successfully"),
                HttpStatus.OK
        );
    }
}
