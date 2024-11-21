package dev.realtards.kuenyawz.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.utils.UptimeTracker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Tag(name = "Index Routes", description = "Endpoints for checking the status of the API")
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController extends BaseController {

	private final UptimeTracker uptimeTracker;
	private final ApplicationProperties applicationProperties;

	@Operation(summary = "Get API status",
		description = "Retrieves the status of the API"
	)
	@ApiResponses(
		{
			@ApiResponse(responseCode = "200", description = "Successfully retrieved API status",
				content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
						examples = @ExampleObject(value = """
							{
							    "message": "Welcome to Snack N' Bites API",
							    "status": "running",
							    "system": {
							        "version": "1.0",
							        "repositoryUrl": "https://github.com/vianneynara/wz-snack-n-bites-api",
							        "runningSince": "2024-10-24 19:35:40",
							        "systemTime": "2024-10-24 19:54:18",
							        "upTime": "0 days, 0 hours, 18 minutes, 37 seconds"
							    }
							}
							"""
						)
					),
					@Content(mediaType = MediaType.TEXT_HTML_VALUE,
						examples = @ExampleObject(value = "HTML page displaying API status")
					)
				}
			)
		}
	)
	@GetMapping({"/", "/status"})
	public Object status(HttpServletRequest request, Model model) {
		// Create the JSON response structure
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		LinkedHashMap<String, Object> systemInfo = new LinkedHashMap<>();

		systemInfo.put("version", applicationProperties.getVersion());
		systemInfo.put("repositoryUrl", applicationProperties.getRepositoryUrl());
		systemInfo.put("runningSince", uptimeTracker.hasBeenRunningSince());
		systemInfo.put("systemTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		systemInfo.put("upTime", uptimeTracker.hasBeenRunningFor());

		response.put("message", "Welcome to KuenyaWZ API");
		response.put("status", "running");
		response.put("system", systemInfo);

		// check if client accepts HTML by looking at the Accept header
		String acceptHeader = request.getHeader("Accept");
		boolean wantsHtml = acceptHeader != null && acceptHeader.contains("text/html");

		if (wantsHtml) {
			// convert the response map to pretty JSON string
			ObjectMapper mapper = new ObjectMapper();
			try {
				String jsonString = mapper
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(response);
				model.addAttribute("apiMethod", request.getMethod());
				model.addAttribute("apiPath", request.getRequestURI());
				model.addAttribute("jsonData", jsonString);
				model.addAttribute("githubUrl", applicationProperties.getRepositoryUrl());
			} catch (JsonProcessingException e) {
				model.addAttribute("jsonData", "Error generating JSON");
			}
			return "status";
		}

		// Return JSON response
		return ResponseEntity.ok().body(response);
	}
}