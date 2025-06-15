package com.codX.pos.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Service-specific discount request")
public record ServiceDiscountRequest(
       UUID serviceTypeId,
       DiscountRequest discount
) {}