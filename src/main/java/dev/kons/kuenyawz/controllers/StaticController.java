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
		Model model
	) {
		model.addAttribute("message", message);
		return "test/transaction/success";
	}
}
