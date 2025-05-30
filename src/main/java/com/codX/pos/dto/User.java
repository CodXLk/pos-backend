package com.codX.pos.dto;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public record User(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String password,
        Role role,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        UUID createdUserId,
        UUID modifiedUserId
) {
}
