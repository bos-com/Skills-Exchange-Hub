package com.skillexchange.platform.dto;

import java.util.Set;

public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private String bio;
    private String availability;
    private Set<String> skillIds;

    // Constructors
    public UserProfileDTO() {}

    public UserProfileDTO(String firstName, String lastName, String bio, String availability, Set<String> skillIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.availability = availability;
        this.skillIds = skillIds;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Set<String> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(Set<String> skillIds) {
        this.skillIds = skillIds;
    }
}