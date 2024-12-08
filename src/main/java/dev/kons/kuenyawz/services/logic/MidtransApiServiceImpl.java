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
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.webflux.LogbookExchangeFilterFunction;

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

		LogbookExchangeFilterFunction logbookWebFilter = new LogbookExchangeFilterFunction(Logbook.builder().build());

		this.webClient = webClientBuilder
			.baseUrl(properties.midtrans().getBaseUrlApp())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
			.filter(logbookWebFilter)
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
			log.error("WebClientResponseError: {}", e.getResponseBodyAsString());
			return handleException(e);
		} catch (WebClientRequestException e) {
			log.error("WebClientRequestError", e);
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
			return handleException(e);
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
			return handleException(e);
		}
	}

	@Override
	public MidtransResponse refundTransaction(String orderId) {
		try {
			var wc = ofBaseUrl(properties.midtrans().getBaseUrlApi());
			return wc.post()
				.uri("/v2/{order_id}/refund", orderId)
				.retrieve()
				.bodyToMono(MidtransResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			return handleException(e);
		}
	}

	private WebClient ofBaseUrl(String baseUrl) {
		return webClient.mutate().baseUrl(baseUrl).build();
	}

	private WebClient withAppendedNotification(WebClient wc) {
		return wc.mutate()
			.defaultHeader("X-Append-Notification",
				"https://www.google.com","https://www.youtube.com","https://www.github.com"
			).build();
	}

	private MidtransResponse handleException(WebClientResponseException e) {
		try {
			log.error("Error processing Midtrans call: {}", e.getResponseBodyAsString());
			if (e.getResponseBodyAsString().contains("404")) {
				return fromError(e);
			} else if (e.getResponseBodyAsString().contains("412")) {
				return fromError(e);
			} else if (e.getResponseBodyAsString().contains("418")) {
				return fromError(e);
			} else if (e.getResponseBodyAsString().contains("500")) {
				return fromError(e);
			}

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

	private MidtransResponse fromError(WebClientResponseException e) {
		return MidtransResponse.builder()
			.statusCode(e.getStatusCode().toString())
			.statusMessage(e.getMessage())
			.build();
	}
}
