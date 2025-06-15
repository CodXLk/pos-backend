package com.codX.pos.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Item-specific discount request")
public record ItemDiscountRequest(  // ADD 'public' HERE
                                    UUID itemId,
                                    DiscountRequest discount
) {}
