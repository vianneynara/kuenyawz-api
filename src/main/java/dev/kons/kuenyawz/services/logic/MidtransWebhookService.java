package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.midtrans.MidtransNotification;
import dev.kons.kuenyawz.exceptions.MidtransTransactionException;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import jakarta.validation.constraints.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface MidtransWebhookService {

	/**
	 * Process notification from Midtrans. This method also does authenticity check.
	 *
	 * @param notification Notification from Midtrans
	 */
	void processNotification(MidtransNotification notification);

	/**
	 * Very unsafe, test-only method to create a signature key.
	 *
	 * @param orderId {@link String} the transaction id as defined in {@link OrderingServiceImpl}
	 * @param statusCode {@link String} the status code of the transaction
	 * @param grossAmount {@link String} the gross amount of the transaction
	 * @param merchantServerKey {@link String} the merchant server key
	 * @return {@link String} the generated signature key
	 */
	String signatureCreator(
		@NotNull String orderId,
		@NotNull String statusCode,
		@NotNull String grossAmount,
		String merchantServerKey
	);

	/**
	 * Generate notification for the given purchase id.
	 *
	 * @param purchaseId {@link Long} the purchase id
	 * @return {@link String} the generated notification json body.
	 */
	String generateNotification(Long purchaseId, String transactionStatus, String fraudStatus);

	/**
	 * Validate the signature key of the notification. Will throw an exception if the signature key is invalid.
	 *
	 * @param notification {@link MidtransNotification}
	 * @param merchantServerKey Merchant server key
	 */
	static void validateSignatureKey(MidtransNotification notification, String merchantServerKey) {
		final var receivedSignature = notification.getSignatureKey();
		final var generatedSignature = generateSignatureKey(notification, merchantServerKey);

		if (!receivedSignature.equals(generatedSignature)) {
			throw new UnauthorizedException("Invalid signature key");
		}
	}

	/**
	 * To generate signature key specifically for Midtrans notification.
	 * The order of the parameters follows
	 * <a href="https://docs.midtrans.com/reference/receiving-notifications#signature-key">Midtrans#signature-key</a>,
	 * generated using SHA-512 algorithm.
	 *
	 * @param notification {@link MidtransNotification}
	 * @param merchantServerKey Merchant server key
	 * @return Generated signature key
	 */
	static String generateSignatureKey(MidtransNotification notification, String merchantServerKey) {
		final var orderId = notification.getOrderId();
		final var statusCode = notification.getStatusCode();
		final var grossAmount = notification.getGrossAmount();
		final String raw = orderId + statusCode + grossAmount + merchantServerKey;

		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-512");
			final byte[] messageDigest = md.digest(raw.getBytes());
			final StringBuilder hexString = new StringBuilder();

			for (byte b : messageDigest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new MidtransTransactionException(e.getMessage());
		}
	}
}
