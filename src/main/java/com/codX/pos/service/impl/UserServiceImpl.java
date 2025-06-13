package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateUserRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.exception.UserNameAlreadyExistException;
import com.codX.pos.repository.UserRepository;
import com.codX.pos.service.EmailService;
import com.codX.pos.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public UserEntity createUser(CreateUserRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        // Validate permissions based on current user role
        validateUserCreationPermissions(currentUser, request.role());

        // Validate for duplicates
        validateUserUniqueness(request, null);

        String defaultPassword = generateDefaultPassword();
        UserEntity userEntity = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .userName(request.userName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(passwordEncoder.encode(defaultPassword))
                .role(request.role())
                .companyId(determineCompanyId(currentUser, request))
                .branchId(determineBranchId(currentUser, request))
                .isDefaultPassword(true)
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        // Send welcome email if email is provided
        sendWelcomeEmailIfProvided(request.email(), request.firstName(), defaultPassword);

        log.info("User created successfully: {} with role: {}", savedUser.getUsername(), savedUser.getRole());
        return savedUser;
    }

    @Override
    @Transactional
    public UserEntity createSuperAdmin(CreateUserRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        // Only existing super admin can create another super admin, or initial creation
        if (currentUser != null && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Super Admin can create another Super Admin");
        }

        // For initial super admin creation, currentUser will be null
        if (currentUser == null && userRepository.countByRole(Role.SUPER_ADMIN) > 0) {
            throw new UnauthorizedException("Super Admin already exists");
        }

        // Validate for duplicates
        validateUserUniqueness(request, null);

        String defaultPassword = generateDefaultPassword();
        UserEntity userEntity = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .userName(request.userName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(passwordEncoder.encode(defaultPassword))
                .role(Role.SUPER_ADMIN)
                .isDefaultPassword(true)
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        // Send welcome email if email is provided
        sendWelcomeEmailIfProvided(request.email(), request.firstName(), defaultPassword);

        log.info("Super Admin created successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    public List<UserEntity> getUsersByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();
        validateCompanyAccess(currentUser, companyId);
        return userRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<UserEntity> getUsersByBranch(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();
        validateBranchAccess(currentUser, branchId);
        return userRepository.findByBranchIdAndIsActiveTrue(branchId);
    }

    @Override
    public List<UserEntity> getUsersByRole(Role role, UUID companyId, UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();
        validateGetUserByRolePermissions(currentUser, role);

        if (companyId != null) {
            validateCompanyAccess(currentUser, companyId);
            return userRepository.findByRoleAndCompanyIdAndIsActiveTrue(role, companyId);
        } else if (branchId != null) {
            validateBranchAccess(currentUser, branchId);
            return userRepository.findByRoleAndBranchIdAndIsActiveTrue(role, branchId);
        }
        throw new UnauthorizedException("Invalid access parameters");
    }

    @Override
    public UserEntity getUserById(UUID id) {
        UserEntity userEntity = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserContextDto currentUser = UserContext.getUserContext();
        validateGetUserPermissions(currentUser, userEntity);
        return userEntity;
    }

    @Override
    @Transactional
    public UserEntity updateUser(UUID id, CreateUserRequest request) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserContextDto currentUser = UserContext.getUserContext();

        // Validate access permissions
        validateUserUpdatePermissions(currentUser, existingUser);

        // Validate for duplicates (excluding current user)
        validateUserUniqueness(request, existingUser);

        existingUser.setFirstName(request.firstName());
        existingUser.setLastName(request.lastName());
        existingUser.setUserName(request.userName());
        existingUser.setPhoneNumber(request.phoneNumber());
        existingUser.setEmail(request.email());

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserContextDto currentUser = UserContext.getUserContext();

        validateUserUpdatePermissions(currentUser, user);

        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", user.getUsername());
    }

    @Override
    public String generateDefaultPassword() {
        // For development, use simple password
        return "12345678";

        // For production, use random password generation
        // StringBuilder password = new StringBuilder(8);
        // for (int i = 0; i < 8; i++) {
        //     password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        // }
        // return password.toString();
    }

    /**
     * Validates that username, phone number, and email are unique
     * @param request The user request containing the data to validate
     * @param existingUser The existing user (for updates) or null (for creation)
     */
    private void validateUserUniqueness(CreateUserRequest request, UserEntity existingUser) {
        // Check for existing username
        if (isFieldDuplicate(request.userName(),
                existingUser != null ? existingUser.getUsername() : null,
                userRepository::existsByUserName)) {
            throw new UserNameAlreadyExistException("Username already exists");
        }

        // Check for existing phone number
        if (isFieldDuplicate(request.phoneNumber(),
                existingUser != null ? existingUser.getPhoneNumber() : null,
                userRepository::existsByPhoneNumber)) {
            throw new UserNameAlreadyExistException("Phone number already exists");
        }

        // Check for existing email
        if (request.email() != null && !request.email().trim().isEmpty()) {
            if (isFieldDuplicate(request.email(),
                    existingUser != null ? existingUser.getEmail() : null,
                    userRepository::existsByEmail)) {
                throw new UserNameAlreadyExistException("Email already exists");
            }
        }
    }

    /**
     * Helper method to check if a field value is duplicate
     * @param newValue The new value to check
     * @param currentValue The current value (for updates) or null (for creation)
     * @param existsFunction Function to check if the value exists in database
     * @return true if duplicate, false otherwise
     */
    private boolean isFieldDuplicate(String newValue, String currentValue,
                                     java.util.function.Function<String, Boolean> existsFunction) {
        // If it's an update and the value hasn't changed, it's not a duplicate
        if (currentValue != null && currentValue.equals(newValue)) {
            return false;
        }

        // Check if the new value already exists in database
        return existsFunction.apply(newValue);
    }

    /**
     * Sends welcome email if email is provided
     * @param email The email address
     * @param firstName The user's first name
     * @param password The generated password
     */
    private void sendWelcomeEmailIfProvided(String email, String firstName, String password) {
        if (email != null && !email.trim().isEmpty()) {
            try {
                emailService.sendWelcomeEmail(email, firstName, password);
                log.info("Welcome email sent successfully to: {}", email);
            } catch (Exception e) {
                log.warn("Failed to send welcome email to: {}", email, e);
                // Don't fail user creation if email fails
            }
        }
    }

    private void validateUserCreationPermissions(UserContextDto currentUser, Role roleToCreate) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                if (roleToCreate != Role.SUPER_ADMIN && roleToCreate != Role.COMPANY_ADMIN) {
                    throw new UnauthorizedException("Super Admin can only create Super Admin or Company Admin");
                }
                break;
            case COMPANY_ADMIN:
                if (roleToCreate != Role.BRANCH_ADMIN) {
                    throw new UnauthorizedException("Company Admin can only create Branch Admin");
                }
                break;
            case BRANCH_ADMIN:
                if (roleToCreate != Role.POS_USER && roleToCreate != Role.EMPLOYEE) {
                    throw new UnauthorizedException("Branch Admin can only create POS User or Employee");
                }
                break;
            case POS_USER:
                if (roleToCreate != Role.CUSTOMER) {
                    throw new UnauthorizedException("POS User can only create Customer");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to create users");
        }
    }

    private UUID determineCompanyId(UserContextDto currentUser, CreateUserRequest request) {
        if (currentUser.role() == Role.SUPER_ADMIN) {
            return request.companyId();
        }
        return currentUser.companyId();
    }

    private UUID determineBranchId(UserContextDto currentUser, CreateUserRequest request) {
        if (currentUser.role() == Role.SUPER_ADMIN || currentUser.role() == Role.COMPANY_ADMIN) {
            return request.branchId();
        }
        return currentUser.branchId();
    }

    private void validateCompanyAccess(UserContextDto currentUser, UUID companyId) {
        if(currentUser.role() == Role.BRANCH_ADMIN) {
            throw new UnauthorizedException("Access denied to company data");
        }
        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company data");
        }
    }

    private void validateBranchAccess(UserContextDto currentUser, UUID branchId) {
        if (currentUser.role() == Role.BRANCH_ADMIN || currentUser.role() == Role.POS_USER ||
                currentUser.role() == Role.EMPLOYEE) {
            if (!currentUser.branchId().equals(branchId)) {
                throw new UnauthorizedException("Access denied to branch data");
            }
        }
    }

    private void validateUserUpdatePermissions(UserContextDto currentUser, UserEntity targetUser) {
        switch (currentUser.role()) {
            case SUPER_ADMIN:
                // Super admin can update anyone
                break;
            case COMPANY_ADMIN:
                if (!currentUser.companyId().equals(targetUser.getCompanyId())) {
                    throw new UnauthorizedException("Cannot update user from different company");
                }
                break;
            case BRANCH_ADMIN:
                if (!currentUser.branchId().equals(targetUser.getBranchId())) {
                    throw new UnauthorizedException("Cannot update user from different branch");
                }
                break;
            case POS_USER:
                if (!currentUser.branchId().equals(targetUser.getBranchId()) ||
                        !targetUser.getRole().equals(Role.CUSTOMER)) {
                    throw new UnauthorizedException("Only can update customers with same branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to update user");
        }
    }

    private void validateGetUserPermissions(UserContextDto currentUser, UserEntity targetUser) {
        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                if (!currentUser.companyId().equals(targetUser.getCompanyId())) {
                    throw new UnauthorizedException("Cannot get user from different company");
                }
                break;
            case BRANCH_ADMIN:
                if (!currentUser.branchId().equals(targetUser.getBranchId())) {
                    throw new UnauthorizedException("Cannot get user from different branch");
                }
                break;
            case POS_USER:
                if (!EnumSet.of(Role.CUSTOMER, Role.EMPLOYEE).contains(targetUser.getRole()) ||
                        !currentUser.branchId().equals(targetUser.getBranchId())) {
                    throw new UnauthorizedException("Only can get customers and employees with same branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to get user");
        }
    }
    private void validateGetUserByRolePermissions(UserContextDto user, Role role) {
        switch (user.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                if(EnumSet.of(Role.SUPER_ADMIN, Role.COMPANY_ADMIN).contains(role)) {
                     throw new UnauthorizedException("Cannot get admin and company admin");
                }
                break;
            case BRANCH_ADMIN:
                if(EnumSet.of(Role.SUPER_ADMIN, Role.COMPANY_ADMIN,Role.BRANCH_ADMIN).contains(role)) {
                    throw new UnauthorizedException("Cannot get admin and company admin and branch admin");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to get user");

        }
    }
}
