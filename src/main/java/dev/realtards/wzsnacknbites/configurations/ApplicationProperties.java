package dev.realtards.wzsnacknbites.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {

	// Fields and default values
	private String version = "0.0";
	private String repositoryUrl = "https://github.com/vianneynara/*";
}
