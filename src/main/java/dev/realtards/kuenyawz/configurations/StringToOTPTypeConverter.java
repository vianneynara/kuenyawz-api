package dev.realtards.kuenyawz.configurations;

import dev.realtards.kuenyawz.services.entity.OTPService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class StringToOTPTypeConverter implements Converter<String, OTPService.OTPType> {
	@Override
	public OTPService.OTPType convert(String source) {
		if (source == null || source.trim().isEmpty()) {
			return OTPService.OTPType.NUMERIC;
		}
		return OTPService.OTPType.valueOf(source.toUpperCase());
	}
}