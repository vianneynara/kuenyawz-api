package dev.kons.kuenyawz.services.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.MidtransRequest;
import dev.kons.kuenyawz.dtos.midtrans.MidtransResponse;
import dev.kons.kuenyawz.exceptions.MidtransTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class MidtransApiServiceImpl implements MidtransApiService {

	private final WebClient webClient;
	private final ApplicationProperties properties;
	private final ObjectMapper objectMapper;

	public MidtransApiServiceImpl(WebClient.Builder webClientBuilder, ApplicationProperties properties, ObjectMapper objectMapper) {
		final String authorization = properties.midtrans().getServerKey() + ":";
		final String encodedAuth = Base64.getEncoder().encodeToString(authorization.getBytes());

		this.webClient = webClientBuilder
			.baseUrl(properties.midtrans().getBaseUrlApp())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
			.build();
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	@Override
	public MidtransResponse createTransaction(MidtransRequest request) {
		try {
			return webClient.post()
				.uri("/snap/v1/transactions")
				.bodyValue(request)
				.retrieve()
				.bodyToMono(MidtransResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			return handleException(e);
		} catch (WebClientRequestException e) {
			log.error("Error processing Midtrans transaction", e);
			throw new MidtransTransactionException("Error processing request to Midtrans");
		}
	}

	@Override
	public MidtransResponse fetchTransactionStatus(String orderId) {
		try {
			var wc = ofBaseUrl(properties.midtrans().getBaseUrlApi());
			return wc.get()
				.uri("/v2/{order_id}/status", orderId)
				.retrieve()
				.onStatus(status -> status.value() == 404, ClientResponse::createException)
				.bodyToMono(MidtransResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			if (e.getResponseBodyAsString().contains("404 Not Found")) {
				throw new MidtransTransactionException("Transaction with id " + orderId + " not found in Midtrans");
			} else {
				return handleException(e);
			}
		}
	}

	@Override
	public MidtransResponse cancelTransaction(String orderId) {
		try {
			var wc = ofBaseUrl(properties.midtrans().getBaseUrlApi());
			return wc.post()
				.uri("/v2/{order_id}/cancel", orderId)
				.retrieve()
				.bodyToMono(MidtransResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			if (e.getResponseBodyAsString().contains("404 Not Found")) {
				return MidtransResponse.builder().statusCode("404").build();
			} else {
				return handleException(e);
			}
		}
	}

	private WebClient ofBaseUrl(String baseUrl) {
		return webClient.mutate().baseUrl(baseUrl).build();
	}

	private MidtransResponse handleException(WebClientResponseException e) {
		try {
			log.error("Error processing Midtrans transaction: {}", e.getResponseBodyAsString());
			MidtransResponse response = objectMapper.readValue(
				e.getResponseBodyAsString(),
				MidtransResponse.class
			);
			throw new MidtransTransactionException(null, response);
		} catch (IOException parseException) {
			log.error("Error parsing Midtrans error response", parseException);
			throw new MidtransTransactionException("Error processing Midtrans transaction");
		}
	}
}
