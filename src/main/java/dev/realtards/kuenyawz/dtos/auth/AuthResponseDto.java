package dev.realtards.kuenyawz.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "Authentication response")
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AuthResponseDto {

    @Schema(description = "JWT access token with short expiration time")
    @NotNull
    private String accessToken;

    @Schema(description = "Refresh token for obtaining new access tokens")
    @NotNull
    private String refreshToken;

    @Schema(description = "Token type, always 'Bearer'", example = "Bearer", defaultValue = "Bearer")
    @NotNull
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Account identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accountId;

    @Schema(description = "Email address that also acts as username", example = "emilia@wz.com")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Email
    private String email;

    @Schema(description = "User's full name", example = "Emilia")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullName;

    @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> roles;

    @Schema(description = "Token issue time in seconds", example = "1620000000")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long iat;

    @Schema(description = "Token expiration time in seconds", example = "3600")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long exp;
}