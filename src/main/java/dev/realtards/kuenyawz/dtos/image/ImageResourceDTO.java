package dev.realtards.kuenyawz.dtos.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResourceDTO {

	private Long imageResourceId;

	private String filename;

	private String relativeLocation;
}
