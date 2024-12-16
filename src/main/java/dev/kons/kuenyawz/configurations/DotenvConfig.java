package dev.kons.kuenyawz.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

/**
 * Configuration class for dotenv. This helps to load the dotenv file and
 * make it available for the application. This uses the dotenv-java library.
 */
@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        String[] possibleLocations = {
            System.getProperty("user.dir"),           // Project root
            System.getProperty("user.dir") + "/.env", // Explicit path
            "/app/.env"                               // Docker container path
        };

        for (String location : possibleLocations) {
            try {
                Dotenv dotenv = Dotenv.configure()
                    .directory(location)
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();

                // If a valid .env is found, return it
                if (dotenv != null && !dotenv.entries().isEmpty()) {
                    return dotenv;
                }
            } catch (Exception ignored) {
                // Continue to next location if this fails
            }
        }

        // Fallback to empty Dotenv if no .env file found
        return Dotenv.configure().ignoreIfMissing().load();
    }

    /**
     * Method to override properties with system/environment variables.
     * Necessary for dockerized environments where environment variables are used.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Dotenv dotenv) {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();

        // Add all .env entries as properties, but allow system/environment variables to override
        dotenv.entries().forEach((entry) ->
            properties.setProperty(entry.getKey(), entry.getValue())
        );

        configurer.setProperties(properties);

        // Allow system props/env vars to override .env
        configurer.setLocalOverride(true);
        return configurer;
    }
}
