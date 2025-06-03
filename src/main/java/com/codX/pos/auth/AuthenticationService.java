package com.codX.pos.auth;

import com.codX.pos.config.JwtService;
import com.codX.pos.entity.Role;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        Optional<UserEntity> existingUserOptional = userRepository.findByUserName(registerRequest.getUserName());

        if (existingUserOptional.isPresent()) {
            throw new UserNameAlreadyExistException("User Name Already Exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .isActive(true)
                .isDefaultPassword(false)
                .build();

        userRepository.save(userEntity);
        String jwtToken = jwtService.generateToken(userEntity);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
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

            // Check if user is active
            if (!userEntity.isActive()) {
                throw new UserNameOrPasswordIncorrectException("Account is deactivated");
            }

            String jwtToken = jwtService.generateToken(userEntity);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException ex) {
            throw new UserNameOrPasswordIncorrectException("Username or Password is incorrect");
        }
    }

    public AuthenticationResponse authenticateCustomer(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );

            UserEntity userEntity = userRepository.findByUserName(request.getUserName())
                    .orElseThrow();

            // Ensure only customers can use this endpoint
            if (userEntity.getRole() != Role.CUSTOMER) {
                throw new UserNameOrPasswordIncorrectException("Access denied - Customer login only");
            }

            if (!userEntity.isActive()) {
                throw new UserNameOrPasswordIncorrectException("Account is deactivated");
            }

            String jwtToken = jwtService.generateToken(userEntity);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException ex) {
            throw new UserNameOrPasswordIncorrectException("Username or Password is incorrect");
        }
    }
}
