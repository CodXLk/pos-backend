package com.codX.pos.service;

import com.codX.pos.dto.request.CreateUserRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserEntity createUser(CreateUserRequest request);
    UserEntity createSuperAdmin(CreateUserRequest request);
    List<UserEntity> getUsersByCompany(UUID companyId);
    List<UserEntity> getUsersByBranch(UUID branchId);
    List<UserEntity> getUsersByRole(Role role, UUID companyId, UUID branchId);
    UserEntity getUserById(UUID id);
    UserEntity updateUser(UUID id, CreateUserRequest request);
    void deactivateUser(UUID id);
    String generateDefaultPassword();
}
