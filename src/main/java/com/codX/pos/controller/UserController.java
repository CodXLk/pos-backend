package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateUserRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.service.UserService;
import com.codX.pos.util.StandardResponse;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request);
        return new ResponseEntity<>(
                new StandardResponse(201, user, "User created successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/create-super-admin")
    public ResponseEntity<?> createSuperAdmin(@Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createSuperAdmin(request);
        return new ResponseEntity<>(
                new StandardResponse(201, user, "Super Admin created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<?> getUsersByCompany(@PathVariable UUID companyId) {
        List<UserEntity> users = userService.getUsersByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<?> getUsersByBranch(@PathVariable UUID branchId) {
        List<UserEntity> users = userService.getUsersByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<?> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID branchId) {
        List<UserEntity> users = userService.getUsersByRole(role, companyId, branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, users, "Users retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        UserEntity user = userService.getUserById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, user, "User retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.updateUser(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, user, "User updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "User deactivated successfully"),
                HttpStatus.OK
        );
    }
}
