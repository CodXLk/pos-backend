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
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
public class ItemEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;
    private String description;
    private BigDecimal unitPrice;
    private String unit; // e.g., "liters", "pieces", "kg"
    private Integer stockQuantity;
    private Integer minStockLevel;
    private boolean isActive = true;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID itemCategoryId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID companyId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID branchId;

    @Column(precision = 5, scale = 2)
    private BigDecimal defaultDiscountValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private DiscountType defaultDiscountType = DiscountType.PERCENTAGE;

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
