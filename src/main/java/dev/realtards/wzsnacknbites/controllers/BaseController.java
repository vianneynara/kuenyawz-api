package dev.realtards.wzsnacknbites.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This abstract class acts as the base controllers. Should be extended by
 * all controllers. This is used to add the base URL such as indication of
 * api and api version.
 */
@RequestMapping("/api/v1")
public abstract class BaseController {
}
