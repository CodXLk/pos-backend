// DataInitializer.java (Complete)
package com.codX.pos.config;

import com.codX.pos.dto.request.CreateUserRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.repository.UserRepository;
import com.codX.pos.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Create initial super admin if none exists
        if (userRepository.countByRole(Role.SUPER_ADMIN) == 0) {
            CreateUserRequest superAdminRequest = CreateUserRequest.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .userName("superadmin")
                    .phoneNumber("+94774580710")
                    .email("aurionx@gmail.com")
                    .role(Role.SUPER_ADMIN)
                    .build();

            try {
                userService.createSuperAdmin(superAdminRequest);
                log.info("Initial Super Admin created successfully with username: superadmin");
                log.info("Default password will be generated - check logs for SMS simulation");
            } catch (Exception e) {
                log.error("Failed to create initial Super Admin: {}", e.getMessage());
            }
        } else {
            log.info("Super Admin already exists - skipping initialization");
        }
    }
}
