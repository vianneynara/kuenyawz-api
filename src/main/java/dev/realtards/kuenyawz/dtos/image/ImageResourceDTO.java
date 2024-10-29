package dev.realtards.kuenyawz.dtos.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Image upload request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResourceDTO {

	@Schema(description = "The image resource id")
	Long imageResourceId;

	@Schema(description = "Original image filename")
	String originalFilename;

	@Schema(description = "The image resource filename")
	String filename;

	@Schema(description = "The image resource relative location")
	String relativeLocation;
}
