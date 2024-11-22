package dev.realtards.kuenyawz.dtos.fonnte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageDto {

	private String target;
	private String message;
	private String countryCode;
}
