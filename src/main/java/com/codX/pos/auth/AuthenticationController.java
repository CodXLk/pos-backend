package com.codX.pos.auth;

import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
