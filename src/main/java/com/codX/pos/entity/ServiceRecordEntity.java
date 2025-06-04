package com.codX.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_records")
@EntityListeners(AuditingEntityListener.class)
public class ServiceRecordEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private LocalDateTime serviceDate;
    private Integer currentMileage;
    private String notes;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID customerId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID vehicleId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID invoiceId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID companyId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID branchId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(columnDefinition = "BINARY(16)")
    private UUID createdUserId;

    @LastModifiedBy
    @Column(columnDefinition = "BINARY(16)")
    private UUID modifiedUserId;
}