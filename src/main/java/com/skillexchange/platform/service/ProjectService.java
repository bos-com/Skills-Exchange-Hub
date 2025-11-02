package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.ProjectDTO;
import com.skillexchange.platform.entity.Project;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.repository.ProjectRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.SkillRepository;
import com.skillexchange.platform.repository.UniversityRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UniversityRepository universityRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, 
                         SkillRepository skillRepository, UniversityRepository universityRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.universityRepository = universityRepository;
    }

    public Optional<Project> createProject(String ownerId, String title, String description, 
                               Set<String> requiredSkillIds, Set<String> participantIds, 
                               Set<String> universityIds) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        
        if (ownerOpt.isPresent()) {
            Project project = new Project(title, ownerOpt.get());
            project.setDescription(description);
            
            // Set required skills
            if (requiredSkillIds != null && !requiredSkillIds.isEmpty()) {
                Set<Skill> skills = requiredSkillIds.stream()
                    .map(skillRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setRequiredSkills(skills);
            }
            
            // Set participants
            if (participantIds != null && !participantIds.isEmpty()) {
                Set<User> participants = participantIds.stream()
                    .map(userRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setParticipants(participants);
            }
            
            // Set universities
            if (universityIds != null && !universityIds.isEmpty()) {
                Set<University> universities = universityIds.stream()
                    .map(universityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setUniversities(universities);
            }
            
            return Optional.of(projectRepository.save(project));
        }
        
        return Optional.empty();
    }

    public List<Project> getProjectsByOwner(String ownerId) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        return ownerOpt.map(projectRepository::findByOwner)
                .orElse(List.of());
    }

    public List<Project> getProjectsByUniversity(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        // This would require a custom query method in ProjectRepository
        return List.of(); // Placeholder implementation
    }

    public Optional<Project> updateProject(String projectId, String title, String description, 
                               Set<String> requiredSkillIds, Set<String> participantIds, 
                               Set<String> universityIds) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            if (title != null) project.setTitle(title);
            if (description != null) project.setDescription(description);
            
            // Update required skills
            if (requiredSkillIds != null) {
                Set<Skill> skills = requiredSkillIds.stream()
                    .map(skillRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setRequiredSkills(skills);
            }
            
            // Update participants
            if (participantIds != null) {
                Set<User> participants = participantIds.stream()
                    .map(userRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setParticipants(participants);
            }
            
            // Update universities
            if (universityIds != null) {
                Set<University> universities = universityIds.stream()
                    .map(universityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                project.setUniversities(universities);
            }
            
            return Optional.of(projectRepository.save(project));
        }
        
        return Optional.empty();
    }

    public boolean deleteProject(String projectId) {
        if (projectRepository.existsById(projectId)) {
            projectRepository.deleteById(projectId);
            return true;
        }
        return false;
    }

    public ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setOwnerId(project.getOwner().getId());
        dto.setOwnerName(project.getOwner().getUsername());
        
        if (project.getRequiredSkills() != null) {
            Set<String> skillIds = project.getRequiredSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
            dto.setRequiredSkillIds(skillIds);
        }
        
        if (project.getParticipants() != null) {
            Set<String> participantIds = project.getParticipants().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
            dto.setParticipantIds(participantIds);
        }
        
        if (project.getUniversities() != null) {
            Set<String> universityIds = project.getUniversities().stream()
                .map(University::getId)
                .collect(Collectors.toSet());
            dto.setUniversityIds(universityIds);
        }
        
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }

    public List<ProjectDTO> convertToDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}