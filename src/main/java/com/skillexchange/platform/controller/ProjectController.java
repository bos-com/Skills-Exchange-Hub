package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.ProjectDTO;
import com.skillexchange.platform.entity.Project;
import com.skillexchange.platform.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Create a new project
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'UNIVERSITY_ADMIN')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        Optional<Project> projectOpt = projectService.createProject(
            projectDTO.getOwnerId(),
            projectDTO.getTitle(),
            projectDTO.getDescription(),
            projectDTO.getRequiredSkillIds(),
            projectDTO.getParticipantIds(),
            projectDTO.getUniversityIds()
        );
        
        if (projectOpt.isPresent()) {
            return ResponseEntity.ok(projectService.convertToDTO(projectOpt.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get projects by owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByOwner(@PathVariable String ownerId) {
        List<Project> projects = projectService.getProjectsByOwner(ownerId);
        return ResponseEntity.ok(projectService.convertToDTOList(projects));
    }

    // Get projects by university
    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUniversity(@PathVariable String universityId) {
        List<Project> projects = projectService.getProjectsByUniversity(universityId);
        return ResponseEntity.ok(projectService.convertToDTOList(projects));
    }

    // Update a project
    @PutMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'UNIVERSITY_ADMIN')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable String projectId,
                                                   @Valid @RequestBody ProjectDTO projectDTO) {
        Optional<Project> projectOpt = projectService.updateProject(
            projectId,
            projectDTO.getTitle(),
            projectDTO.getDescription(),
            projectDTO.getRequiredSkillIds(),
            projectDTO.getParticipantIds(),
            projectDTO.getUniversityIds()
        );
        
        if (projectOpt.isPresent()) {
            return ResponseEntity.ok(projectService.convertToDTO(projectOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a project
    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'UNIVERSITY_ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectId) {
        boolean deleted = projectService.deleteProject(projectId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}