package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.services.entity.TransactionService;
import dev.kons.kuenyawz.services.logic.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Transactions", description = "Endpoints for transactions")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

	private final TransactionService transactionService;

//	@Operation(summary = "Get transactions")
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
//	})
//	@SecurityRequirement(name = "cookieAuth")
//	@GetMapping
	public ResponseEntity<?> getTransactions(
		@RequestParam(required = false) Boolean asc,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String paymentType,
		@RequestParam(required = false) Long purchaseId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer pageSize
	) {
		var criteria = TransactionService.TransactionSearchCriteria.of(
			asc, status, paymentType, purchaseId, from, to, page, pageSize
		);
		Page<TransactionDto> result;
		if (AuthService.isAuthenticatedUser()) {
			Account account = AuthService.getAuthenticatedAccount();
			result = transactionService.findAll(account.getAccountId(), criteria);
		} else {
			result = transactionService.findAll(criteria);
		}
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Fetch transaction status")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Transaction status fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@GetMapping("/{transactionId}")
	public ResponseEntity<?> fetchTransactionStatus(
		@PathVariable Long transactionId
	) {
		TransactionDto transactionDto = transactionService.fetchTransaction(transactionId);
		return ResponseEntity.ok(transactionDto);
	}

//	@Operation(summary = "Cancel a transaction")
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "Transaction canceled successfully"),
//		@ApiResponse(responseCode = "400", description = "Bad request")
//	})
//	@PostMapping("/{transactionId}/cancel")
	public ResponseEntity<?> cancelTransaction(
		@PathVariable Long transactionId
	) {
		transactionService.cancelOne(transactionId);
		return ResponseEntity.ok().build();
	}
}
