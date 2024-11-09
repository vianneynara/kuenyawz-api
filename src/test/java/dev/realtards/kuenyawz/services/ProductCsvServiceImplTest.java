package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.csv.ProductCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCsvServiceImplTest {

	ProductCsvService productCsvService = new ProductCsvServiceImpl();

	@Test
	void testCsvToProductCsvRecord() throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:seeders/Products.csv");

		// Act
		List<ProductCsvRecord> productCsvRecords = productCsvService.csvToProductCsvRecord(file);

		// Assertions
		assertThat(productCsvRecords.size()).isGreaterThan(0);
	}
}