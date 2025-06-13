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
@Table(name = "service_items")
public class ServiceItemEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID serviceRecordDetailId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID itemId;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID companyId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID branchId;
}
