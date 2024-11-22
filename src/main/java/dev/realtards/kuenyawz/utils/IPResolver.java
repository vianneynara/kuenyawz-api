package dev.realtards.kuenyawz.utils;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

@Deprecated
@Component
@RequiredArgsConstructor
public class IPResolver {

	private final ApplicationProperties properties;

	@Value("${server.port:8081}")
	private String serverPort;

	/**
	 * Gets the primary IP address of the machine
	 */
	public String getPrimaryIpAddress() {
		try {
			System.out.println("IP address: " + InetAddress.getLocalHost().getHostAddress());
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return properties.getPublicIp();
		}
	}

	/**
	 * Gets the full URL where the application is running
	 */
	public String getApplicationUrl() {
		String ip;
		try {
			ip = getPublicIp();
		} catch (IOException e) {
			ip = getPrimaryIpAddress();
		}
		return String.format("%s://%s:%s", properties.getHttpProtocol(), ip, serverPort);
	}

	/**
	 * Gets the public IP address of the machine
	 */
	public String getPublicIp() throws IOException {
		try {
			URL url = new URI("https://api.ipify.org").toURL();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
				return reader.readLine();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
