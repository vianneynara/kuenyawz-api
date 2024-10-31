package dev.realtards.kuenyawz.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Simulator", description = "Simulator pages")
@Controller
@RequestMapping("/sim")
public class SimulatorController extends BaseController {

	@Operation(summary = "Returns the batch upload form page")
	@GetMapping("/batch-upload-form")
	public String batchUploadForm() {
		return "batch-upload-form";
	}
}
