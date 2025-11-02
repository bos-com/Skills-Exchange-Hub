package com.skillexchange.platform.dto;

import com.skillexchange.platform.entity.PartnershipProposal;

import java.time.LocalDateTime;

public class PartnershipProposalDTO {
    private String id;
    private String title;
    private String description;
    private String proposingUniversityId;
    private String proposingUniversityName;
    private String targetUniversityId;
    private String targetUniversityName;
    private PartnershipProposal.ProposalStatus status;
    private LocalDateTime proposedAt;
    private LocalDateTime respondedAt;
    private String proposerId;
    private String proposerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PartnershipProposalDTO() {}

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

    public String getProposingUniversityId() {
        return proposingUniversityId;
    }

    public void setProposingUniversityId(String proposingUniversityId) {
        this.proposingUniversityId = proposingUniversityId;
    }

    public String getProposingUniversityName() {
        return proposingUniversityName;
    }

    public void setProposingUniversityName(String proposingUniversityName) {
        this.proposingUniversityName = proposingUniversityName;
    }

    public String getTargetUniversityId() {
        return targetUniversityId;
    }

    public void setTargetUniversityId(String targetUniversityId) {
        this.targetUniversityId = targetUniversityId;
    }

    public String getTargetUniversityName() {
        return targetUniversityName;
    }

    public void setTargetUniversityName(String targetUniversityName) {
        this.targetUniversityName = targetUniversityName;
    }

    public PartnershipProposal.ProposalStatus getStatus() {
        return status;
    }

    public void setStatus(PartnershipProposal.ProposalStatus status) {
        this.status = status;
    }

    public LocalDateTime getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(LocalDateTime proposedAt) {
        this.proposedAt = proposedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getProposerId() {
        return proposerId;
    }

    public void setProposerId(String proposerId) {
        this.proposerId = proposerId;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
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