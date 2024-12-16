package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.midtrans.MidtransNotification;
import dev.kons.kuenyawz.services.logic.MidtransWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Midtrans webhook", description = "Midtrans webhook endpoints")
@RestController
@RequestMapping("/midtrans")
@RequiredArgsConstructor
@Slf4j
public class MidtransWebhookController {

	private final MidtransWebhookService midtransWebhookService;

	@Operation(summary = "Midtrans Notifier",
		description = "Used by midtrans to notify the server of payment state changes")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notification has been received"),
	})
	@PostMapping("/notify")
	public ResponseEntity<?> midtransNotifier(
		@RequestBody MidtransNotification notification
	) {
		midtransWebhookService.processNotification(notification);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Sign Notification",
		description = "Used to sign notification for midtrans")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notification has been signed"),
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/sign")
	public ResponseEntity<Map<String, String>> signNotification(
		@RequestParam String orderId,
		@RequestParam String statusCode,
		@RequestParam String grossAmount,
		@RequestParam(required = false) String merchantServerKey
	) {
		final String signatureKey = midtransWebhookService.signatureCreator(orderId, statusCode, grossAmount, merchantServerKey);
		return ResponseEntity.ok(Map.of("signatureKey", signatureKey));
	}

	@Operation(summary = "Generate Notification",
		description = "Used to generate notification for midtrans")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notification has been generated"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "404", description = "Purchase not found"),
	})
	@PostMapping("/generate")
	public ResponseEntity<String> generateNotification(
		@RequestParam Long purchaseId,
		@RequestParam(required = false) String transactionStatus,
		@RequestParam(required = false) String fraudStatus
	) {
		return ResponseEntity.ok(midtransWebhookService.generateNotification(purchaseId, transactionStatus, fraudStatus));
	}
}
