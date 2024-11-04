package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Data Transfer Object for importing products from CSV")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCsvPostDto {

	@Schema(description = "CSV file to be imported")
	@NotNull(message = "File must be provided")
	MultipartFile file;
}
