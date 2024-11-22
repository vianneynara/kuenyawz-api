package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Schema(description = "Privilege update request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrivilegeUpdateDto {

	@Schema(description = "New privilege level to set",	example = "user")
	@NotBlank(message = "Privilege is required")
	@Pattern(regexp = "^(ADMIN|USER)$", message = "Privilege must be either 'ADMIN' or 'USER'")
	@CleanString
	private String privilege;

	public Account.Privilege getPrivilege() {
		return Account.Privilege.fromString(privilege);
	}

	public PrivilegeUpdateDto(Account.Privilege privilege) {
		this.privilege = privilege.getPrivilege();
	}
}
