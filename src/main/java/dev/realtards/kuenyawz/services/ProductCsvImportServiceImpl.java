package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductCsvPostDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceExistsException;
import dev.realtards.kuenyawz.exceptions.ResourceUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCsvImportServiceImpl implements ProductCsvImportService {

	private final int CSV_VARIANT_COLUMNS_COUNT = 3;
	private final int CSV_VARIANT_STARTS_AT = 6;

	private final ProductService productService;

	@Override
	public void importProductsFromCsv(MultipartFile file) {
		if (!Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1).equals("csv")) {
			throw new InvalidRequestBodyValue("Must be a valid CSV file");
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			// Skip the header on row 1
			String line = br.readLine();

			while ((line = br.readLine()) != null) {
                try {
                    ProductPostDto productDto = parseLineToProductDto(line, null);
                    if (productDto != null && !productDto.getVariants().isEmpty()) {
                        productService.createProduct(productDto);
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
	public void importProductsFromDto(ProductCsvPostDto productCsvPostDto) {
		importProductsFromCsv(productCsvPostDto.getFile());
	}

	@Override
	public ProductPostDto parseLineToProductDto(String line, String separator) {
		if (separator == null || separator.isEmpty()) {
			separator = ";";
		}

		String[] values = line.split(separator);

		if ((values.length == 0) || values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty() || values[3].isEmpty() || values[4].isEmpty() || values[5].isEmpty()) {
			log.warn("Skipping product with empty required fields");
			return null;
		}

		List<VariantPostDto> variants = new ArrayList<>();

		for (int i = 0; i < CSV_VARIANT_COLUMNS_COUNT; i += 2) {
			int currIdx = CSV_VARIANT_STARTS_AT + (i * 2);

			if ((currIdx + 1 < values.length) && !values[currIdx].isEmpty() && !values[currIdx + 1].isEmpty()) {
				VariantPostDto variant = VariantPostDto.builder()
					.type(values[currIdx])
					.price(new BigDecimal(values[currIdx + 1]))
					.build();
				variants.add(variant);
			}
		}

		ProductPostDto dto = ProductPostDto.builder()
			.name(values[0])
			.tagline(values[1])
			.description(values[2])
			.category(values[3])
			.minQuantity(Integer.valueOf(values[4]))
			.maxQuantity(Integer.valueOf(values[5]))
			.variants(variants)
			.build();
		return dto;
	}

	@ExceptionHandler({NumberFormatException.class})
	public void handleNumberFormatException(NumberFormatException e) {
		throw new InvalidRequestBodyValue("Parsing failed, please check the values and separator of the file");
	}
}
