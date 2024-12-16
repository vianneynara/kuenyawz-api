package dev.kons.kuenyawz.services.logic;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.dtos.csv.AccountCsvRecord;
import dev.kons.kuenyawz.exceptions.ResourceUploadException;
import dev.kons.kuenyawz.services.entity.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCsvServiceImpl implements AccountCsvService {

	private final AccountService accountService;

	@Override
	public void saveAccountFromFile(String path) {
		try {
			File file = ResourceUtils.getFile(path);
			saveAccountFromFile(file);
		} catch (FileNotFoundException e) {
			log.error("Path not found: {}", e.getMessage());
			throw new ResourceUploadException("File not found: " + e.getMessage());
		}
	}

	@Override
	public void saveAccountFromFile(File file) {
		try {
			List<AccountCsvRecord> records = csvToAccountCsvRecord(new FileInputStream(file));
			processAccountRecords(records);
		} catch (FileNotFoundException e) {
			log.error("File not found: {}", e.getMessage());
			throw new ResourceUploadException("File not found: " + e.getMessage());
		}
	}

	@Override
	public void saveAccountFromStream(InputStream inputStream) {
		List<AccountCsvRecord> records = csvToAccountCsvRecord(inputStream);
		processAccountRecords(records);
	}

	private List<AccountCsvRecord> csvToAccountCsvRecord(InputStream inputStream) {
		return new CsvToBeanBuilder<AccountCsvRecord>(
			new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			.withType(AccountCsvRecord.class)
			.withSeparator(';')
			.withIgnoreLeadingWhiteSpace(true)
			.build()
			.parse();
	}

	private void processAccountRecords(List<AccountCsvRecord> records) {
		int successCount = 0;
		int skipCount = 0;
		int errorCount = 0;

		for (AccountCsvRecord record : records) {
			try {
				final var accountRegistrationDto = fromRecord(record);
				accountService.createAccount(accountRegistrationDto);
				successCount++;
			} catch (Exception e) {
				log.error("Error importing account: {}", e.getMessage());
				errorCount++;
			}
		}

		log.info("Import completed - success: {}, skipped: {}, errors: {}",
			successCount, skipCount, errorCount);
		log.info("Total accounts in database: {}",
			accountService.getAllAccounts().size());
	}

	private AccountRegistrationDto fromRecord(AccountCsvRecord record) {
		return AccountRegistrationDto.builder()
			.password(record.getPassword())
			.fullName(record.getFullName())
			.phone(record.getPhone())
			.build();
	}
}
