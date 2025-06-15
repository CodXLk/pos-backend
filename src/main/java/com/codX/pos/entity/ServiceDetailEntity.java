package com.codX.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_details")
public class ServiceDetailEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID serviceRecordId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID serviceTypeId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID itemId; // Optional - for items used in service

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private String notes;

    @Enumerated(EnumType.STRING)
    private ServiceDetailType type; // SERVICE or ITEM

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID companyId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID branchId;
}
