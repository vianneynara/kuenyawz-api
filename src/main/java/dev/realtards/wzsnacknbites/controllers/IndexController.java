package dev.realtards.wzsnacknbites.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.realtards.wzsnacknbites.utils.UptimeTracker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

	private final UptimeTracker uptimeTracker;

	private final String VERSION = "1.0";
	private final String REPOSITORY = "https://github.com/vianneynara/wz-snack-n-bites-api";

	public IndexController(UptimeTracker uptimeTracker) {
		this.uptimeTracker = uptimeTracker;
	}

	@GetMapping({"/", "/status"})
	public Object status(HttpServletRequest request, Model model) {
		// Create the JSON response structure
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		LinkedHashMap<String, Object> systemInfo = new LinkedHashMap<>();

		systemInfo.put("version", VERSION);
		systemInfo.put("repository", REPOSITORY);
		systemInfo.put("systemTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		systemInfo.put("upTime", uptimeTracker.hasBeenRunningFor());

		response.put("message", "Welcome to Snack N' Bites API");
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
				model.addAttribute("githubUrl", REPOSITORY);
			} catch (JsonProcessingException e) {
				model.addAttribute("jsonData", "Error generating JSON");
			}
			return "status";
		}

		// Return JSON response
		return ResponseEntity.ok().body(response);
	}
}