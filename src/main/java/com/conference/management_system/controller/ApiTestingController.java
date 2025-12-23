package com.conference.management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Exposes the root route and forwards it to the static API testing dashboard.
 * This keeps the dashboard available without adding template engines.
 */
@Controller
public class ApiTestingController {

    @GetMapping("/")
    public String showDashboard() {
        return "forward:/api-testing-dashboard.html";
    }
}
