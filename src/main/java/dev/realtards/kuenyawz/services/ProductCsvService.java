package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.csv.ProductCsvRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ProductCsvService {

	/**
	 * Executes the process of parsing from a CSV file with semicolon separated values
	 * and saves it to the database.
	 * @param mpf {@link MultipartFile} CSV file to be imported
	 * @throws IOException
	 */
	void saveProductFromMultipartFile(MultipartFile mpf);

	void saveProductFromFile(String path);

	void saveProductFromFile(File file);
}
