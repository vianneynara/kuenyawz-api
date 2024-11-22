package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Secure account information returned by the API")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSecureDto {

    @Schema(description = "Unique identifier of the account", example = "1221991247904768")
    private Long accountId;

    @Schema(description = "User's full name", example = "Emilia")
    private String fullName;

    @Schema(description = "User's phone number", example = "81234567890")
    private String phone;

    @Schema(description = "User's email address", example = "emilia@example.com")
    private String email;

    @Schema(description = "User's privilege level", example = "user")
    private Account.Privilege privilege;
}
