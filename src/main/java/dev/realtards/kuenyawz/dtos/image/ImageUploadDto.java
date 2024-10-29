package dev.realtards.kuenyawz.dtos.image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Image upload request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadDto {

	@Schema(description = "Image file")
	@NotNull
	private MultipartFile file;
}