package dev.kons.kuenyawz.services.logic;

import java.io.File;

public interface AccountCsvService {
	/**
	 * Executes the process of parsing from a CSV file path.
	 *
	 * @param path {@link String} path to the CSV file to be imported
	 */
	void saveAccountFromFile(String path);

	/**
	 * Executes the process of parsing from a CSV file.
	 *
	 * @param file {@link File} CSV file to be imported
	 */
	void saveAccountFromFile(File file);
}
