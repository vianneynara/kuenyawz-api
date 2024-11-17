package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

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

    @Schema(description = "User's email address", example = "emilia@example.com")
    private String email;

    @Schema(description = "Timestamp of when email was verified", example = "2024-01-01T12:00:00")
    private LocalDateTime emailVerifiedAt;

    @Schema(description = "User's phone number", example = "12345678901")
    private String phone;

    @Schema(description = "User's privilege level", example = "user")
    private Account.Privilege privilege;
}
