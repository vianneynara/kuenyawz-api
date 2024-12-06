package dev.kons.kuenyawz.dtos.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCsvRecord {

	@CsvBindByName
	String phone;

	@CsvBindByName
	String fullName;

	@CsvBindByName
	String password;
}
