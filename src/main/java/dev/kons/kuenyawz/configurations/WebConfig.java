package dev.kons.kuenyawz.configurations;

import dev.kons.kuenyawz.filters.RequestLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.addPathPrefix("/api", c -> true);
	}

	@Bean
	public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
		FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new RequestLoggingFilter());
		registrationBean.addUrlPatterns("*");  // apply to all URLs
		registrationBean.setOrder(1);  // high priority

		return registrationBean;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/");
	}
}