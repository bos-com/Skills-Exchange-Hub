package com.skillexchange.platform.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "workshops")
public class Workshop {
    @Id
    private String id;

    private String title;

    private String description;

    @DBRef
    private University hostUniversity;

    private LocalDateTime dateTime;

    private int capacity;

    private int registeredCount = 0;

    @DBRef
    private Set<User> registeredStudents;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public Workshop() {}

    public Workshop(String title, University hostUniversity, LocalDateTime dateTime, int capacity) {
        this.title = title;
        this.hostUniversity = hostUniversity;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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

    public University getHostUniversity() {
        return hostUniversity;
    }

    public void setHostUniversity(University hostUniversity) {
        this.hostUniversity = hostUniversity;
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

    public Set<User> getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(Set<User> registeredStudents) {
        this.registeredStudents = registeredStudents;
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

    // Helper methods
    public boolean isFull() {
        return registeredCount >= capacity;
    }

    public boolean hasSpace() {
        return registeredCount < capacity;
    }

    public void incrementRegisteredCount() {
        if (hasSpace()) {
            this.registeredCount++;
        }
    }

    public void decrementRegisteredCount() {
        if (registeredCount > 0) {
            this.registeredCount--;
        }
    }
}