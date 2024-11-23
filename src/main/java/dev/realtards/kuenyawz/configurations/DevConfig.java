package dev.realtards.kuenyawz.configurations;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * Configuration class for development environment. This helps handling non-existent
 * .env file under development environment. Why dev? Because in the real hosting environment,
 * this can be set by the secure secrets' management.
 * */
@Configuration
@Profile("dev")
public class DevConfig {
    
    @PostConstruct
    public void validateEnvFile() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            throw new RuntimeException(
                "No .env file found! Please copy .env.example to .env and fill in the values.");
        }
    }
}