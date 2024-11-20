package dev.realtards.kuenyawz.utils;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@RequiredArgsConstructor
public class IPResolver {

	private final ApplicationProperties properties;

    @Value("${server.port:8081}")
    private String serverPort;

    /**
     * Gets the primary IP address of the machine
     */
    public String getPrimaryIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Gets the full URL where the application is running
     */
    public String getApplicationUrl() throws UnknownHostException {
        String ip = getPrimaryIpAddress();
        return String.format("%s://%s:%s", properties.getHttpProtocol(), ip, serverPort);
    }
}
