package dev.realtards.kuenyawz.services;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.realtards.kuenyawz.configurations.properties.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.csv.ProductCsvRecord;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceExistsException;
import dev.realtards.kuenyawz.exceptions.ResourceUploadException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public class ProductCsvServiceImpl implements ProductCsvService {

	private final int CSV_VARIANT_COLUMNS_COUNT = 3;
	private final int CSV_VARIANT_STARTS_AT = 4;

	private final ProductService productService;
	private final ApplicationProperties applicationProperties;

	@Override
	public List<ProductCsvRecord> csvToProductCsvRecord(File file) {
		try {
			List<ProductCsvRecord> productCsvRecords = new CsvToBeanBuilder<ProductCsvRecord>(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
				.withType(ProductCsvRecord.class)
				.withSeparator(';')
				.withIgnoreLeadingWhiteSpace(true)
				.build().parse();
			return productCsvRecords;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void importProductsFromMultiPartFile(MultipartFile file) {
		if (!Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1).equals("csv")) {
			throw new InvalidRequestBodyValue("Must be a valid CSV file");
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			// Skip the header on row 1
			String line = br.readLine();

			while ((line = br.readLine()) != null) {
				try {
					ProductPostDto productPostDto = parseLineToProductPostDto(line, null);
					if (productPostDto != null && !productPostDto.getVariants().isEmpty()) {
						productService.createProduct(productPostDto);
					}
				} catch (InvalidRequestBodyValue | ResourceExistsException e) {
					// Log the error and skip the current line
					log.warn("Error importing product: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			throw new ResourceUploadException("Error reading the file");
		}
	}

	@Override
	public ProductPostDto parseLineToProductPostDto(String line, String separator) {
		if (separator == null || separator.isEmpty()) {
			separator = ";";
		}

		String[] values = line.split(separator);

		if ((values.length == 0) || values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty() || values[3].isEmpty() || values[4].isEmpty() || values[5].isEmpty()) {
			log.warn("Skipping product with empty required fields");
			return null;
		}

		List<VariantPostDto> variants = parseVariantsFromValues(values);

		ProductPostDto dto = ProductPostDto.builder()
			.name(values[0])
			.tagline(values[1])
			.description(values[2])
			.category(values[3])
			.variants(variants)
			.build();
		return dto;
	}

	@ExceptionHandler({NumberFormatException.class})
	public void handleNumberFormatException(NumberFormatException e) {
		throw new InvalidRequestBodyValue("Parsing failed, please check the values and separator of the file");
	}

	// Helper methods to iterate over the variants of a line

	private List<VariantPostDto> parseVariantsFromValues(String[] values) {
		try {
			List<VariantPostDto> variants = new ArrayList<>();
			for (int i = 0; i < CSV_VARIANT_COLUMNS_COUNT; i++) {
				int currIdx = CSV_VARIANT_STARTS_AT + (i * 3);

				if ((currIdx + 1 < values.length) && !values[currIdx].isEmpty() && !values[currIdx + 1].isEmpty()) {
					VariantPostDto variantPostDto = VariantPostDto.builder()
						.type(values[currIdx])
						.price(new BigDecimal(values[currIdx + 1]))
						.maxQuantity(applicationProperties.getMaxVariantQuantity())
						.build();

					try {
						// Check for min quantity in the row
						if (values[currIdx + 2] != null && !values[currIdx + 2].isEmpty()) {
							variantPostDto.setMinQuantity(Integer.parseInt(values[currIdx + 2]));
						}
					} catch (IndexOutOfBoundsException e) {
						// When the min quantity is not defined, use default value.
						variantPostDto.setMinQuantity(1);
					}

					// Check consistency, max quantity should be defined manually, uses default value.
					if (variantPostDto.isQuantityConsistent()) {
						variants.add(variantPostDto);
					} else {
						log.warn("Skipping variant with inconsistent quantity: " + variantPostDto.getType());
					}
				}
			}
			return variants;
		} catch (IndexOutOfBoundsException e) {
			log.warn("Skipping product with incomplete variant data");
			return null;
		}
	}
}
