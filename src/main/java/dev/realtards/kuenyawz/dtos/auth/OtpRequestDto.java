package dev.realtards.kuenyawz.dtos.auth;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for OTP request")
@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class OtpRequestDto {

    @Schema(description = "User's valid phone number", example = "81234567890", pattern = "^[1-9][0-9]{7,14}$")
    @Pattern(regexp = "^[1-9][0-9]{7,14}$", message = "Invalid phone number format")
	@NotBlank(message = "Phone number is required")
	@CleanString
	private String phone;

	@Schema(description = "User's IP address", example = "1.1.1.1")
	@Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Invalid IP address format")
	@CleanString
	private String ipAddress;
}
