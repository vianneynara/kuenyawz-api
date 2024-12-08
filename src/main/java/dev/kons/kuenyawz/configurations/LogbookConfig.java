package dev.kons.kuenyawz.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

/**
 * Configures the logstash to properly format the logbook logs.
 * Without this, the JSON logs will escape the double quotes and unable
 * to be parsed.
 * <br><br>
 * <a href="https://www.udemy.com/course/spring-framework-6-beginner-to-guru/learn/lecture/44051012">
 *     <b>Udemy Reference</b></a>
 */
@Configuration
public class LogbookConfig {

	@Bean
	public Sink logbookLogStash() {
		HttpLogFormatter formatter = new JsonHttpLogFormatter();
		LogstashLogbackSink sink = new LogstashLogbackSink(formatter);
		return sink;
	}
}
