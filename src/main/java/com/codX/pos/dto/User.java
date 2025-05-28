package com.codX.pos.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

@Builder(toBuilder = true)
public record User(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        @Enumerated(EnumType.STRING)
        Role role
) {
}
