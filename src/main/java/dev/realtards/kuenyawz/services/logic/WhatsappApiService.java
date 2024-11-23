package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.dtos.fonnte.SendMessageDto;

public interface WhatsappApiService {

	String send(String target, String message, String countryCode);

	String send(SendMessageDto sendMessageDto);
}
