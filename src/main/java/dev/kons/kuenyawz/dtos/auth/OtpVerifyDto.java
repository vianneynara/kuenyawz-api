package dev.kons.kuenyawz.dtos.auth;

import dev.kons.kuenyawz.utils.stringtrimmer.CleanString;
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
public class OtpVerifyDto {

    @Schema(description = "User's valid phone number", example = "81234567890", pattern = "^[1-9][0-9]{7,14}$")
    @Pattern(regexp = "^[1-9][0-9]{7,14}$", message = "Invalid phone number format")
	@NotBlank(message = "Phone number is required")
	@CleanString
	private String phone;

    @Schema(description = "OTP code", example = "123456", pattern = "^[0-9a-zA-Z]{6}$")
    @Pattern(regexp = "^[0-9a-zA-Z]{6}$", message = "Invalid OTP format")
	@NotBlank(message = "OTP code is required")
	@CleanString
	private String otp;
}
