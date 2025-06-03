package com.codX.pos.controller;

import com.codX.pos.auth.AuthenticationRequest;
import com.codX.pos.auth.AuthenticationResponse;
import com.codX.pos.auth.AuthenticationService;
import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Customer Portal", description = "Customer-specific operations and service history access")
public class CustomerController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "Customer login",
            description = "Dedicated login endpoint for customers. Only users with CUSTOMER role can login through this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer logged in successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "data": {
                            "token": "eyJhbGciOiJIUzI1NiJ9..."
                        },
                        "message": "Customer logged in successfully"
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Access denied - Customer login only")
    })
    public ResponseEntity<?> customerLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "userName": "customer1",
                            "password": "customerPassword"
                        }
                        """)
                    )
            )
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticateCustomer(request);
        return new ResponseEntity<>(
                new StandardResponse(200, response, "Customer logged in successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/service-history")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get customer service history",
            description = "Retrieve the service history for the authenticated customer. This endpoint will be implemented to show past services and transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - Customer access only")
    })
    public ResponseEntity<?> getServiceHistory() {
        // TODO: Implement service history retrieval
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Service history retrieved successfully"),
                HttpStatus.OK
        );
    }
}
