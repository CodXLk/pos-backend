package com.codX.pos.auth;

import com.codX.pos.config.JwtService;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.exception.EmailAlreadyExistException;
import com.codX.pos.exception.EmailOrPasswordIncorrectException;
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

    public AuthenticationResponse register(RegisterRequest registerRequest){
        Optional<UserEntity> existingUserOptional = userRepository.findByEmail(registerRequest.getEmail());
        if(existingUserOptional.isPresent()){
            throw new EmailAlreadyExistException("Email Already Exists");
        }else{
            UserEntity userEntity = UserEntity.builder()
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .email(registerRequest.getEmail())
                    .password(registerRequest.getPassword())
//                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .build();
            userRepository.save(userEntity);

            String jwtToken = jwtService.generateToken(userEntity);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            String jwtToken = jwtService.generateToken(userEntity);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException ex) {
            throw new EmailOrPasswordIncorrectException("Email or Password is incorrect");
        }
    }
}
