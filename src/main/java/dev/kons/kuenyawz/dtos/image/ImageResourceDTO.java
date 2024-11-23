package dev.kons.kuenyawz.dtos.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Data Transfer Object for Image Resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResourceDTO {

	@Schema(description = "The image resource id")
	private Long imageResourceId;

	@Schema(description = "Original image filename")
	private String originalFilename;

	@Schema(description = "The image resource filename")
	private String filename;

	@Schema(description = "The image resource relative location")
	private String relativeLocation;
}