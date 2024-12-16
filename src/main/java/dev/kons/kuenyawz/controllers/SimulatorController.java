package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.services.entity.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Simulator", description = "Simulator pages")
@Controller
@RequestMapping("/sim")
@RequiredArgsConstructor
public class SimulatorController extends BaseController {

	private final ProductService productService;

	@Operation(summary = "Returns the batch upload form page")
	@GetMapping("/batch-upload-form")
	public String batchUploadForm(Model model) {
		Page<ProductDto> products = productService.getAllProductsPaginated(
			null, null, null, 0, 100
		);
		model.addAttribute("products", products);
		return "batch-upload-form";
	}
}
