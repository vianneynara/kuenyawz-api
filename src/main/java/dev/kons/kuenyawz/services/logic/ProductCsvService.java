package dev.kons.kuenyawz.services.logic;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ProductCsvService {
	/**
	 * Executes the process of parsing from a CSV file with semicolon separated values
	 * and saves it to the database.
	 * @param mpf {@link MultipartFile} CSV file to be imported
	 */
	void saveProductFromMultipartFile(MultipartFile mpf);

	void saveProductFromFile(String path);

	void saveProductFromFile(File file);

	void saveProductFromStream(InputStream inputStream);
}
