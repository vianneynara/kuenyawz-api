package dev.kons.kuenyawz.dtos.image;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response object for list of resource information DTOs")
public record ListOfImageResourceDto(

	@Schema(description = "List of image resource information DTOs")
	List<ImageResourceDTO> images
) {
}
