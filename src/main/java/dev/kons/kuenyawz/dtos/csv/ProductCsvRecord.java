package dev.kons.kuenyawz.dtos.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCsvRecord {

	@CsvBindByName
	String name;

	@CsvBindByName
	String tagline;

	@CsvBindByName
	String description;

	@CsvBindByName
	String category;

	@CsvBindByName
	String variant1_type;

	@CsvBindByName
	Float variant1_price;

	@CsvBindByName
	Integer variant1_minQuantity;

	@CsvBindByName
	String variant2_type;

	@CsvBindByName
	Float variant2_price;

	@CsvBindByName
	Integer variant2_minQuantity;

	@CsvBindByName
	String variant3_type;

	@CsvBindByName
	Float variant3_price;

	@CsvBindByName
	Integer variant3_minQuantity;
}
