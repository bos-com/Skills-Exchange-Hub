package com.skillexchange.platform.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentAccess() {
        return "Student Content.";
    }

    @GetMapping("/university")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public String universityAccess() {
        return "University Admin Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public String adminAccess() {
        return "Platform Admin Board.";
    }
}