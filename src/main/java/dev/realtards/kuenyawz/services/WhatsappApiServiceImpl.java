package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.fonnte.SendMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WhatsappApiServiceImpl implements WhatsappApiService {

	private final WebClient webClient;
	private final ApplicationProperties properties;

	public WhatsappApiServiceImpl(WebClient.Builder webClientBuilder, ApplicationProperties properties) {
		this.webClient = webClientBuilder
			.baseUrl("https://api.fonnte.com")
			.build();
		this.properties = properties;
	}

	@Override
	public String send(String target, String message, String countryCode) {
		SendMessageDto sendMessageDto = SendMessageDto.builder()
			.target(target)
			.message(message)
			.countryCode(countryCode)
			.build();

		String response = send(sendMessageDto);
		return response;
	}

	@Override
	public String send(SendMessageDto sendMessageDto) {
		return webClient.post()
			.uri("/send")
			.header("Authorization", properties.getSecurity().getFonnteApiToken())
			.bodyValue(sendMessageDto)
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}
}
