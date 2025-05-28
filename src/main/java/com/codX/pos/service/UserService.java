package com.codX.pos.service;

import com.codX.pos.dto.User;
import com.codX.pos.entity.UserEntity;

public interface UserService {
    UserEntity create(User user);
}
