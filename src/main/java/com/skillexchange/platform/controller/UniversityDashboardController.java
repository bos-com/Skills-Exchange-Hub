package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.UniversityDashboardDTO;
import com.skillexchange.platform.service.UniversityDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class UniversityDashboardController {

    @Autowired
    private UniversityDashboardService universityDashboardService;

    // Get university dashboard
    @GetMapping("/university/{universityId}")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<UniversityDashboardDTO> getUniversityDashboard(@PathVariable String universityId) {
        UniversityDashboardDTO dashboard = universityDashboardService.getUniversityDashboard(universityId);
        
        if (dashboard != null) {
            return ResponseEntity.ok(dashboard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}