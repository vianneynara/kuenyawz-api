package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.csv.ProductCsvRecord;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ProductCsvService {

	List<ProductCsvRecord> csvToProductCsvRecord(File file);

	/**
	 * Executes the process of parsing from a CSV file with semicolon separated values
	 * and saves it to the database.
	 * @param mpf {@link MultipartFile} CSV file to be imported
	 * @throws IOException
	 */
	void importProductsFromMultiPartFile(MultipartFile mpf);

	/**
	 * Parses a line of string, assumed to be data row of Product CSV with semicolon separated
	 * values file to ProductPostDto.
	 * @param line {@link String} data row of the CSV file
	 * @return {@link ProductPostDto} parsed data to ProductPostDto
	 */
	ProductPostDto parseLineToProductPostDto(String line, String separator);
}
