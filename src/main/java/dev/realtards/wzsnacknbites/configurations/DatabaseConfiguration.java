package dev.realtards.wzsnacknbites.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuration class for database. Configures the database based on the running
 * profile, if no specific profile is provided, the program will run with in memory
 * H2 database.
 */
@Configuration
@RequiredArgsConstructor
public class DatabaseConfiguration {

	private final ApplicationProperties applicationProperties;

	@Bean
	@Profile("postgres")
	public DataSource postgresDataSource() {
		return DataSourceBuilder.create()
			.url(applicationProperties.getDatabase().getUrl())
			.username(applicationProperties.getDatabase().getUsername())
			.password(applicationProperties.getDatabase().getPassword())
			.driverClassName("org.postgresql.Driver")
			.build();
	}

	@Bean
	@Profile("!postgres")
	public DataSource defaultDataSource() {
		return DataSourceBuilder.create()
			.url("jdbc:h2:mem:kuenyawz")
			.username("kuenyawz")
			.password("")
			.driverClassName("org.h2.Driver")
			.build();
	}
}
