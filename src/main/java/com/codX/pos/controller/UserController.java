package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateUserRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.service.UserService;
import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "User Management", description = "Role-based user management operations with hierarchical access control")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create a new user",
            description = """
            Create a new user with role-based restrictions following the hierarchy:
            - Super Admin can create Company Admin
            - Company Admin can create Branch Admin
            - Branch Admin can create POS User and Employee
            - POS User can create Customer
            
            The system automatically assigns companyId and branchId based on the current user's context.
            A default password is generated and the user must change it using OTP verification.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions for this role creation"),
            @ApiResponse(responseCode = "409", description = "Username or phone number already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation details with role-based restrictions",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateUserRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "firstName": "Jane",
                            "lastName": "Smith",
                            "userName": "janesmith",
                            "phoneNumber": "+1234567890",
                            "role": "BRANCH_ADMIN",
                            "email": "jane.smith@example.com",
                            "companyId": "123e4567-e89b-12d3-a456-426614174000",
                            "branchId": "123e4567-e89b-12d3-a456-426614174001"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request);
        return new ResponseEntity<>(
                new StandardResponse(201, user, "User created successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/create-super-admin")
    @Operation(
            summary = "Create Super Admin",
            description = "Create the initial Super Admin user. This endpoint is public for initial system setup. Only one Super Admin can be created initially."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Super Admin created successfully"),
            @ApiResponse(responseCode = "409", description = "Super Admin already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createSuperAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Super Admin creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateUserRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "firstName": "Super",
                            "lastName": "Admin",
                            "userName": "superadmin",
                            "phoneNumber": "+1234567890",
                            "role": "SUPER_ADMIN"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createSuperAdmin(request);
        return new ResponseEntity<>(
                new StandardResponse(201, user, "Super Admin created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Get users by company",
            description = "Retrieve all active users belonging to a specific company. Super Admin can access any company, Company Admin can only access their own company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to company data"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> getUsersByCompany(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID companyId) {
        List<UserEntity> users = userService.getUsersByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get users by branch",
            description = "Retrieve all active users belonging to a specific branch. Access is restricted based on user role and branch association."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to branch data"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getUsersByBranch(
            @Parameter(description = "Branch ID", example = "123e4567-e89b-12d3-a456-426614174001")
            @PathVariable UUID branchId) {
        List<UserEntity> users = userService.getUsersByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get users by role",
            description = "Retrieve users by role with optional company and branch filtering. Useful for finding users with specific roles within organizational boundaries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "400", description = "Invalid role specified")
    })
    public ResponseEntity<?> getUsersByRole(
            @Parameter(description = "User role", example = "BRANCH_ADMIN")
            @PathVariable Role role,
            @Parameter(description = "Company ID for filtering (optional)", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam(required = false) UUID companyId,
            @Parameter(description = "Branch ID for filtering (optional)", example = "123e4567-e89b-12d3-a456-426614174001")
            @RequestParam(required = false) UUID branchId) {
        List<UserEntity> users = userService.getUsersByRole(role, companyId, branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their ID. Access is restricted based on organizational hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174002")
            @PathVariable UUID id) {
        UserEntity user = userService.getUserById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, user, "User retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Update user",
            description = "Update user information. Users can only be updated by those with appropriate permissions in the hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username or phone number already exists")
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174002")
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.updateUser(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, user, "User updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivate a user account (soft delete). The user will no longer be able to login but their data is preserved."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> deactivateUser(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174002")
            @PathVariable UUID id) {
        userService.deactivateUser(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "User deactivated successfully"),
                HttpStatus.OK
        );
    }
}
