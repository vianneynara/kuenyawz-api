package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.midtrans.MidtransNotification;
import dev.kons.kuenyawz.services.logic.MidtransWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
