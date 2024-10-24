package dev.realtards.wzsnacknbites.dtos.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Password update request")
@Getter
@AllArgsConstructor
public class PasswordUpdateDto {

    @Schema(description = "User's current password", example = "emiliaBestGirl")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password to set", example = "NaraXEmilia1")
    @NotBlank(message = "New password is required")
    private String newPassword;

    @Schema(description = "Confirmation of new password", example = "NaraXEmilia1")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}