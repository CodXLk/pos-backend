package com.codX.pos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(
        description = "User authentication request for login",
        example = """
        {
            "userName": "johndoe",
            "password": "password123"
        }
        """
)
public class AuthenticationRequest {

    @NotBlank(message = "Username is required")
    @Schema(
            description = "Username for login",
            example = "johndoe",
            required = true
    )
    private String userName;

    @NotBlank(message = "Password is required")
    @Schema(
            description = "User password",
            example = "password123",
            required = true
    )
    private String password;
}
