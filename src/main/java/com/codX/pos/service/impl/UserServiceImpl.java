package com.codX.pos.service.impl;

import com.codX.pos.dto.User;
import com.codX.pos.entity.UserEntity;
import com.codX.pos.repository.UserRepository;
import com.codX.pos.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity create(User user) {
        User updatedUser = user.toBuilder()
                .password(passwordEncoder.encode(user.password()))
                .build();
        return userRepository.save(objectMapper.convertValue(updatedUser,UserEntity.class));
    }
}
