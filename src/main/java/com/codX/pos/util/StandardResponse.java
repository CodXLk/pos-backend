package com.codX.pos.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Standard API response wrapper",
        example = """
        {
            "code": 200,
            "data": {...},
            "message": "Operation completed successfully"
        }
        """
)
public class StandardResponse {

    @Schema(
            description = "HTTP status code",
            example = "200",
            minimum = "100",
            maximum = "599"
    )
    private int code;

    @Schema(
            description = "Response data (can be any type)",
            example = "{\"id\": \"123\", \"name\": \"Example\"}"
    )
    private Object data;

    @Schema(
            description = "Response message describing the operation result",
            example = "Operation completed successfully"
    )
    private String message;
}
