package dev.realtards.kuenyawz.dtos.image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "List of image upload request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImageUploadDto {

	@Schema(description = "List of image files")
	@Size(min = 1, message = "At least one image is required")
	@Size(max = 3, message = "At most 3 images are allowed")
	private List<ImageUploadDto> images;
}
