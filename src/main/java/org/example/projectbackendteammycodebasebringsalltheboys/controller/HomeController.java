package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
            "status", "Online",
            "message", "School Portal Backend is running",
            "database", "PostgreSQL Connected"
        );
    }
}
