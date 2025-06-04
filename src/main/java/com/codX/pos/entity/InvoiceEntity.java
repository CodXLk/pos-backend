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
@Table(name = "invoices")
@EntityListeners(AuditingEntityListener.class)
public class InvoiceEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    private InvoiceType type; // SERVICE, ITEM_SALE, MIXED

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID customerId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID vehicleId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID serviceRecordId;

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
