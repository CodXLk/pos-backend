package com.codX.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otp_records")
public class OtpEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String phoneNumber;
    private String otpCode;
    private LocalDateTime expiryTime;
    private boolean isUsed;
    private String purpose; // PASSWORD_RESET, PHONE_VERIFICATION

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    private LocalDateTime createdDate;
}
