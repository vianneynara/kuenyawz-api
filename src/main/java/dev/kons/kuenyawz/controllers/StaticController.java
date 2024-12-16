package dev.kons.kuenyawz.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Static Routes", description = "Endpoints for serving static files/pages")
@Controller
@RequestMapping("/static")
@RequiredArgsConstructor
public class StaticController {

	@RequestMapping("/redirect/template")
	public Object transactionSuccess(
		@RequestParam(value = "message", required = false) String message,
		@RequestParam(value = "order_id", required = false) String orderId,
		@RequestParam(value = "status_code", required = false) String statusCode,
		@RequestParam(value = "transaction_status", required = false) String transactionStatus,
		Model model
	) {
		model.addAttribute("message", message);
		model.addAttribute("orderId", orderId);
		model.addAttribute("statusCode", statusCode);
		model.addAttribute("transactionStatus", transactionStatus);
		return "test/transaction/success";
	}
}
