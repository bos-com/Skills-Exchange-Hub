package com.skillexchange.platform.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ProjectDTO {
    private String id;
    private String title;
    private String description;
    private String ownerId;
    private String ownerName;
    private Set<String> requiredSkillIds;
    private Set<String> participantIds;
    private Set<String> universityIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ProjectDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Set<String> getRequiredSkillIds() {
        return requiredSkillIds;
    }

    public void setRequiredSkillIds(Set<String> requiredSkillIds) {
        this.requiredSkillIds = requiredSkillIds;
    }

    public Set<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(Set<String> participantIds) {
        this.participantIds = participantIds;
    }

    public Set<String> getUniversityIds() {
        return universityIds;
    }

    public void setUniversityIds(Set<String> universityIds) {
        this.universityIds = universityIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}