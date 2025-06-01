package com.codX.pos.auth;

import com.codX.pos.config.JwtService;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.exception.UserNameAlreadyExistException;
import com.codX.pos.exception.UserNameOrPasswordIncorrectException;
import com.codX.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest){
        Optional<UserEntity> existingUserOptional = userRepository.findByUserName(registerRequest.getUserName());
        if(existingUserOptional.isPresent()){
            throw new UserNameAlreadyExistException("User Name Already Exists");
        }else{
            UserEntity userEntity = UserEntity.builder()
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .userName(registerRequest.getUserName())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .tenantId(registerRequest.getTenantId())  // <-- Add tenantId
                    .branchId(registerRequest.getBranchId())  // <-- Add branchId
                    .build();
            userRepository.save(userEntity);

            // Prepare extra claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("tenantId", registerRequest.getTenantId());
            extraClaims.put("branchId", registerRequest.getBranchId());

            String jwtToken = jwtService.generateToken(extraClaims, userEntity);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );

            UserEntity userEntity = userRepository.findByUserName(request.getUserName())
                    .orElseThrow();

            Map<String, Object> extraClaims = new HashMap<>();
            // Use tenantId and branchId from request or user entity as you prefer
            extraClaims.put("tenantId", request.getTenantId() != null ? request.getTenantId() : userEntity.getTenantId());
            extraClaims.put("branchId", request.getBranchId() != null ? request.getBranchId() : userEntity.getBranchId());

            String jwtToken = jwtService.generateToken(extraClaims, userEntity);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException ex) {
            throw new UserNameOrPasswordIncorrectException("Username or Password is incorrect");
        }
    }
}
