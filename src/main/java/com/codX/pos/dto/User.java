package com.codX.pos.dto;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public record User(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,

        @Enumerated(EnumType.STRING)
        Role role,

        @CreatedDate
        LocalDateTime createdDate,

        @LastModifiedDate
        LocalDateTime lastModifiedDate
) {
}
