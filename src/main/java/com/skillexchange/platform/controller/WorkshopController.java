package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.WorkshopDTO;
import com.skillexchange.platform.entity.Workshop;
import com.skillexchange.platform.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@CrossOrigin(origins = "*")
public class WorkshopController {

    @Autowired
    private WorkshopService workshopService;

    // Create a new workshop (University Admin only)
    @PostMapping
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<WorkshopDTO> createWorkshop(@Valid @RequestBody WorkshopDTO workshopDTO) {
        Workshop workshop = workshopService.createWorkshop(
            workshopDTO.getTitle(),
            workshopDTO.getDescription(),
            workshopDTO.getHostUniversityId(),
            workshopDTO.getDateTime(),
            workshopDTO.getCapacity()
        );
        
        if (workshop != null) {
            return ResponseEntity.ok(workshopService.convertToDTO(workshop));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all workshops
    @GetMapping
    public ResponseEntity<List<WorkshopDTO>> getAllWorkshops() {
        List<Workshop> workshops = workshopService.getUpcomingWorkshops();
        return ResponseEntity.ok(workshopService.convertToDTOList(workshops));
    }

    // Get workshops by university
    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<WorkshopDTO>> getWorkshopsByUniversity(@PathVariable String universityId) {
        List<Workshop> workshops = workshopService.getWorkshopsByUniversity(universityId);
        return ResponseEntity.ok(workshopService.convertToDTOList(workshops));
    }

    // Get upcoming workshops by university
    @GetMapping("/university/{universityId}/upcoming")
    public ResponseEntity<List<WorkshopDTO>> getUpcomingWorkshopsByUniversity(@PathVariable String universityId) {
        List<Workshop> workshops = workshopService.getUpcomingWorkshopsByUniversity(universityId);
        return ResponseEntity.ok(workshopService.convertToDTOList(workshops));
    }

    // Get workshops by student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<WorkshopDTO>> getWorkshopsByStudent(@PathVariable String studentId) {
        List<Workshop> workshops = workshopService.getWorkshopsByStudent(studentId);
        return ResponseEntity.ok(workshopService.convertToDTOList(workshops));
    }

    // Update a workshop (University Admin only)
    @PutMapping("/{workshopId}")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<WorkshopDTO> updateWorkshop(@PathVariable String workshopId, 
                                                     @Valid @RequestBody WorkshopDTO workshopDTO) {
        Workshop workshop = workshopService.updateWorkshop(
            workshopId,
            workshopDTO.getTitle(),
            workshopDTO.getDescription(),
            workshopDTO.getDateTime(),
            workshopDTO.getCapacity()
        );
        
        if (workshop != null) {
            return ResponseEntity.ok(workshopService.convertToDTO(workshop));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a workshop (University Admin only)
    @DeleteMapping("/{workshopId}")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<Void> deleteWorkshop(@PathVariable String workshopId) {
        boolean deleted = workshopService.deleteWorkshop(workshopId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Register for a workshop (Students only)
    @PostMapping("/{workshopId}/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WorkshopDTO> registerForWorkshop(@PathVariable String workshopId,
                                                          @RequestParam String studentId) {
        Workshop workshop = workshopService.registerStudent(workshopId, studentId);
        if (workshop != null) {
            return ResponseEntity.ok(workshopService.convertToDTO(workshop));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unregister from a workshop (Students only)
    @DeleteMapping("/{workshopId}/unregister")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WorkshopDTO> unregisterFromWorkshop(@PathVariable String workshopId,
                                                             @RequestParam String studentId) {
        Workshop workshop = workshopService.unregisterStudent(workshopId, studentId);
        if (workshop != null) {
            return ResponseEntity.ok(workshopService.convertToDTO(workshop));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}