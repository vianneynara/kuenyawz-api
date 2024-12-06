package dev.kons.kuenyawz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.dtos.product.VariantPostDto;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.entity.ProductService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerIT {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
//		productRepository.deleteAll();
//		productRepository.flush();
	}

	@Test
	void testCreateProduct() throws Exception {
		// Arrange
		ProductPostDto productPostDto = ProductPostDto.builder()
			.name("Test Product1")
			.tagline("Test Tagline")
			.description("Test Description")
			.category("cake")
			.build();

		List<VariantPostDto> variantPostDtos = new ArrayList<>(
			List.of(
				VariantPostDto.builder()
					.price(new BigDecimal("100.00"))
					.type("Test Type")
					.minQuantity(1)
					.maxQuantity(10)
					.build()
			)
		);
		productPostDto.setVariants(variantPostDtos);

		// Act
		MvcResult result = mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productPostDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Test Product1"))
			.andReturn();

		// Assert
		assertThat(result.getResponse().getContentAsString()).contains("Test Product1");
	}


    @Test
    void testGetAllProducts() throws Exception {
		// Insert new product
		insertNewProduct("Test Product1");

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
			.andReturn();
//            .andDo(print()); // This helps with debugging

		// Assert
		assertThat(result.getResponse().getContentAsString()).contains("Test Product1");
	}

	void insertNewProduct(@NotNull String name) {
		ProductPostDto productPostDto = ProductPostDto.builder()
			.name(name)
			.tagline("Test Tagline")
			.description("Test Description")
			.category("cake")
			.build();

		List<VariantPostDto> variantPostDtos = new ArrayList<>(
			List.of(
				VariantPostDto.builder()
					.price(new BigDecimal("10000.00"))
					.type("Test Type")
					.minQuantity(1)
					.maxQuantity(10)
					.build()
			)
		);
		productPostDto.setVariants(variantPostDtos);

		productService.createProduct(productPostDto);
	}
}