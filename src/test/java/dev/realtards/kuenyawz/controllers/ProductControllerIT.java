package dev.realtards.kuenyawz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.services.ProductService;
import dev.realtards.kuenyawz.services.VariantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private ProductService productService;

	@Mock
	private VariantService variantService;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	void testCreateProduct() throws Exception {
		// Arrange
		ProductPostDto productPostDto = ProductPostDto.builder()
			.name("Test Product")
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
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
			.andReturn();

		// Assert
		assertThat(result.getResponse().getContentAsString()).contains("Test Product");
	}

	@Test
	void testGetAllProducts() throws Exception {
		// Arrange
		ProductDto productDto = ProductDto.builder()
			.productId(1L)
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.CAKE)
			.build();

		List<ProductDto> productDtos = List.of(productDto);

		when(productService.getAllProducts(null)).thenReturn(productDtos);

		// Act
		MvcResult result = mockMvc.perform(get("/api/products"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		// Assert
		assertThat(result.getResponse().getContentAsString()).contains("Test Product");
	}
}