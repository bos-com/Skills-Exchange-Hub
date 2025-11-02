package com.skillexchange.platform.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "partnership_proposals")
public class PartnershipProposal {
    public enum ProposalStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    private String id;

    private String title;

    private String description;

    @DBRef
    private University proposingUniversity;

    @DBRef
    private University targetUniversity;

    private ProposalStatus status = ProposalStatus.PENDING;

    private LocalDateTime proposedAt;

    private LocalDateTime respondedAt;

    @DBRef
    private User proposer;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public PartnershipProposal() {}

    public PartnershipProposal(String title, University proposingUniversity, University targetUniversity, User proposer) {
        this.title = title;
        this.proposingUniversity = proposingUniversity;
        this.targetUniversity = targetUniversity;
        this.proposer = proposer;
        this.proposedAt = LocalDateTime.now();
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

    public University getProposingUniversity() {
        return proposingUniversity;
    }

    public void setProposingUniversity(University proposingUniversity) {
        this.proposingUniversity = proposingUniversity;
    }

    public University getTargetUniversity() {
        return targetUniversity;
    }

    public void setTargetUniversity(University targetUniversity) {
        this.targetUniversity = targetUniversity;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public void setStatus(ProposalStatus status) {
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

    public User getProposer() {
        return proposer;
    }

    public void setProposer(User proposer) {
        this.proposer = proposer;
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