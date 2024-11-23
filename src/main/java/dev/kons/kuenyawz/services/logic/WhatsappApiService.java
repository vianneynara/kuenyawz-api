package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.fonnte.SendMessageDto;

public interface WhatsappApiService {

	String send(String target, String message, String countryCode);

	String send(SendMessageDto sendMessageDto);
}
