package com.skillexchange.platform.dto;

import java.util.Set;

public class MatchDTO {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private Set<String> skillsOffered;
    private Set<String> skillsWanted;
    private int matchScore;

    // Constructors
    public MatchDTO() {}

    public MatchDTO(String userId, String username, String firstName, String lastName, String bio, 
                   Set<String> skillsOffered, Set<String> skillsWanted, int matchScore) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.skillsOffered = skillsOffered;
        this.skillsWanted = skillsWanted;
        this.matchScore = matchScore;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Set<String> getSkillsOffered() {
        return skillsOffered;
    }

    public void setSkillsOffered(Set<String> skillsOffered) {
        this.skillsOffered = skillsOffered;
    }

    public Set<String> getSkillsWanted() {
        return skillsWanted;
    }

    public void setSkillsWanted(Set<String> skillsWanted) {
        this.skillsWanted = skillsWanted;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}