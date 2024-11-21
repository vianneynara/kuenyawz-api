package dev.realtards.kuenyawz.services.logic;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.realtards.kuenyawz.dtos.csv.ProductCsvRecord;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceUploadException;
import dev.realtards.kuenyawz.services.entity.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCsvServiceImpl implements ProductCsvService {

    private final ProductService productService;

    @Override
    public void saveProductFromMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestBodyValue("File cannot be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new InvalidRequestBodyValue("Must be a valid CSV file");
        }

        try {
            List<ProductCsvRecord> records = csvToProductCsvRecord(file.getInputStream());
            processProductRecords(records);
        } catch (IOException e) {
            log.error("Error processing CSV file: {}", e.getMessage());
            throw new ResourceUploadException("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public void saveProductFromFile(String path) {
        try {
            File file = ResourceUtils.getFile(path);
            saveProductFromFile(file);
        } catch (FileNotFoundException e) {
            log.error("Path not found: {}", e.getMessage());
            throw new ResourceUploadException("File not found: " + e.getMessage());
        }
    }

    @Override
    public void saveProductFromFile(File file) {
        try {
            List<ProductCsvRecord> records = csvToProductCsvRecord(new FileInputStream(file));
            processProductRecords(records);
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", e.getMessage());
            throw new ResourceUploadException("File not found: " + e.getMessage());
        }
    }

    private List<ProductCsvRecord> csvToProductCsvRecord(InputStream inputStream) {
        return new CsvToBeanBuilder<ProductCsvRecord>(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withType(ProductCsvRecord.class)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
    }

    private void processProductRecords(List<ProductCsvRecord> records) {
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (ProductCsvRecord record : records) {
            try {
                List<VariantPostDto> variants = fromRecord(record);
                if (variants.isEmpty()) {
                    log.warn("Skipping product with no variants: {}", record.getName());
                    skipCount++;
                    continue;
                }

                ProductPostDto productPostDto = ProductPostDto.builder()
                        .name(record.getName())
                        .tagline(record.getTagline())
                        .description(record.getDescription())
                        .category(record.getCategory())
                        .variants(variants)
                        .build();

                productService.createProduct(productPostDto);
                successCount++;
            } catch (Exception e) {
                log.warn("Error importing product {}: {}", record.getName(), e.getMessage());
                errorCount++;
            }
        }

        log.info("Import completed - Success: {}, Skipped: {}, Errors: {}",
                successCount, skipCount, errorCount);
        log.info("Total products in database: {}",
                productService.getAllProducts(null, null).size());
    }

    private List<VariantPostDto> fromRecord(ProductCsvRecord record) {
        List<VariantPostDto> variants = new ArrayList<>();

        addVariantIfValid(variants, record.getVariant1_type(), record.getVariant1_price(),
                record.getVariant1_minQuantity());
        addVariantIfValid(variants, record.getVariant2_type(), record.getVariant2_price(),
                record.getVariant2_minQuantity());
        addVariantIfValid(variants, record.getVariant3_type(), record.getVariant3_price(),
                record.getVariant3_minQuantity());

        return variants;
    }

    private void addVariantIfValid(List<VariantPostDto> variants, String type,
            Float price, Integer minQuantity) {
        if (type != null && price != null) {
            variants.add(VariantPostDto.builder()
                    .type(type)
                    .price(BigDecimal.valueOf(price))
                    .minQuantity(minQuantity != null ? minQuantity : 1)
                    .maxQuantity(250)
                    .build());
        }
    }
}