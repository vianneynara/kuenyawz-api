package dev.kons.kuenyawz.services.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.TransactionRequest;
import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;
import dev.kons.kuenyawz.exceptions.MidtransTransactionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

@Service
public class MidtransApiServiceImpl implements MidtransApiService {

	private final WebClient webClient;
	private final ApplicationProperties properties;
	private final ObjectMapper objectMapper;

	public MidtransApiServiceImpl(WebClient.Builder webClientBuilder, ApplicationProperties properties, ObjectMapper objectMapper) {
		final String authorization = properties.midtrans().getServerKey() + ":";
		final String encodedAuth = Base64.getEncoder().encodeToString(authorization.getBytes());

		this.webClient = webClientBuilder
			.baseUrl(properties.midtrans().getBaseUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
			.build();
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	@Override
	public TransactionResponse createTransaction(TransactionRequest request) {
		return webClient.post()
			.uri("/snap/v1/transactions")
			.bodyValue(request)
			.retrieve()
			.bodyToMono(TransactionResponse.class)
			.retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
				.maxBackoff(Duration.ofSeconds(10))
				.filter(err -> err instanceof WebClientResponseException)
				.onRetryExhaustedThrow((spec, sig) -> new MidtransTransactionException())
			)
			.block();
	}

	@Override
	public TransactionResponse fetchTransactionStatus(String orderId) {
		try {
			return webClient.get()
				.uri("/v2/{order_id}/status", orderId)
				.retrieve()
				.bodyToMono(TransactionResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			return handleException(e);
		}
	}

	@Override
	public TransactionResponse cancelTransaction(String orderId) {
		try {
			return webClient.post()
				.uri("/v2/{order_id}/cancel", orderId)
				.retrieve()
				.bodyToMono(TransactionResponse.class)
				.block();
		} catch (WebClientResponseException e) {
			return handleException(e);
		}
	}

	private TransactionResponse handleException(WebClientResponseException e) {
		try {
			TransactionResponse response = objectMapper.readValue(
				e.getResponseBodyAsString(),
				TransactionResponse.class
			);
			throw new MidtransTransactionException(null, response);
		} catch (IOException parseException) {
			throw new RuntimeException("Error processing Midtrans transaction", e);
		}
	}
}