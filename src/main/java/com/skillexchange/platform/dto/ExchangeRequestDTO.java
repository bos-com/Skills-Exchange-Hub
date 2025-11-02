package com.skillexchange.platform.dto;

import com.skillexchange.platform.entity.ExchangeRequest;

import java.time.LocalDateTime;

public class ExchangeRequestDTO {
    private String id;
    private String requesterId;
    private String requesterName;
    private String recipientId;
    private String recipientName;
    private String offeredSkillId;
    private String offeredSkillName;
    private String requestedSkillId;
    private String requestedSkillName;
    private String message;
    private ExchangeRequest.RequestStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;

    // Constructors
    public ExchangeRequestDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getOfferedSkillId() {
        return offeredSkillId;
    }

    public void setOfferedSkillId(String offeredSkillId) {
        this.offeredSkillId = offeredSkillId;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
    }

    public String getRequestedSkillId() {
        return requestedSkillId;
    }

    public void setRequestedSkillId(String requestedSkillId) {
        this.requestedSkillId = requestedSkillId;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public void setRequestedSkillName(String requestedSkillName) {
        this.requestedSkillName = requestedSkillName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExchangeRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeRequest.RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}