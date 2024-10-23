package dev.realtards.wzsnacknbites.responses;

import java.util.List;

public record ErrorResponseWithErrors(String message, List<String> errors) {
}
