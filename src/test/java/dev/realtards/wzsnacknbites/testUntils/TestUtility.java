package dev.realtards.wzsnacknbites.testUntils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TestUtility {

	public static final String BASE_URL = "/api/v1/";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts any object to a byte array representation of its JSON form.
     * Useful for MockMvc test requests that require byte[] content.
     *
     * @param obj The object to convert to JSON bytes
     * @param <T> The type of the object
     * @return byte array representation of the object's JSON
     * @throws RuntimeException if serialization fails
     */
    public static <T> byte[] asJsonBytes(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON bytes", e);
            throw new RuntimeException("Failed to convert object to JSON bytes", e);
        }
    }

    /**
     * Converts any object to its JSON string representation.
     * Useful for debugging or logging purposes.
     *
     * @param obj The object to convert to JSON string
     * @param <T> The type of the object
     * @return JSON string representation of the object
     * @throws RuntimeException if serialization fails
     */
    public static <T> String asJsonString(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON string", e);
            throw new RuntimeException("Failed to convert object to JSON string", e);
        }
    }
}
