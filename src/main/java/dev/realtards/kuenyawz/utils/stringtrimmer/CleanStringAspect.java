package dev.realtards.kuenyawz.utils.stringtrimmer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * This {@link Aspect} class is used to clean {@link String} fields annotated with {@link CleanString} before
 * being processed by the controller. Current configuration supports {@link org.springframework.web.bind.annotation.PostMapping}
 * and {@link org.springframework.web.bind.annotation.PatchMapping} methods.
 */
@Aspect
@Component
@Slf4j
public class CleanStringAspect {

	/**
	 * Doing the execution for POST and PATCH methods.
	 */
	@Before(
		"execution(* *.*(..)) && (" +
		"@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
		"@annotation(org.springframework.web.bind.annotation.PatchMapping)" +
			")"
	)
	public void cleanStrings(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args == null) {
			return;
		}

		for (Object arg : args) {
			if (arg == null) continue;
			processFields(arg);
		}
	}

	/**
	 * Method to process the fields of an object and clean the {@link String} fields annotated with
	 * {@link CleanString}.
	 * @param object {@link Object} to be processed
	 */
	private void processFields(Object object) {
		try {
			Class<?> clazz = object.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(CleanString.class) && field.getType() == String.class) {
					field.setAccessible(true);
					String value = (String) field.get(object);
					if (value != null) {
						String cleanedValue = value.trim().replaceAll("\\s+", " ");
						field.set(object, cleanedValue);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error processing CleanString fields", e);
		}
	}
}