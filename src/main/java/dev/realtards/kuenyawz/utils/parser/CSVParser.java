package dev.realtards.kuenyawz.utils.parser;

import dev.realtards.kuenyawz.services.ProductCsvImportService;
import dev.realtards.kuenyawz.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CSVParser {

	private final ProductService productService;
	private final ProductCsvImportService productCsvImportService;

	public void saveProductsFromCsv(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			File file = resource.getFile();

			try (FileInputStream fis = new FileInputStream(file)) {
				MultipartFile multipartFile = new MockMultipartFile(
					"file",
					file.getName(),
					"text/csv",
					fis
				);
				productCsvImportService.importProductsFromCsv(multipartFile);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		log.info("Database has {} products", productService.getAllProducts(null).size());
	}
}
