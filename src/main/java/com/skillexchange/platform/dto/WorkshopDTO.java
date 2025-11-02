package com.skillexchange.platform.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class WorkshopDTO {
    private String id;
    private String title;
    private String description;
    private String hostUniversityId;
    private String hostUniversityName;
    private LocalDateTime dateTime;
    private int capacity;
    private int registeredCount;
    private Set<String> registeredStudentIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public WorkshopDTO() {}

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

    public String getHostUniversityId() {
        return hostUniversityId;
    }

    public void setHostUniversityId(String hostUniversityId) {
        this.hostUniversityId = hostUniversityId;
    }

    public String getHostUniversityName() {
        return hostUniversityName;
    }

    public void setHostUniversityName(String hostUniversityName) {
        this.hostUniversityName = hostUniversityName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public Set<String> getRegisteredStudentIds() {
        return registeredStudentIds;
    }

    public void setRegisteredStudentIds(Set<String> registeredStudentIds) {
        this.registeredStudentIds = registeredStudentIds;
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