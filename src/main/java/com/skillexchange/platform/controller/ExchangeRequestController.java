package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.ExchangeRequestDTO;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.service.ExchangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/exchange-requests")
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    public ExchangeRequestController(ExchangeRequestService exchangeRequestService) {
        this.exchangeRequestService = exchangeRequestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ExchangeRequest> createRequest(@RequestBody CreateRequestRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requesterId = authentication.getName(); // Assuming username is the user ID

        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestService.createRequest(
            requesterId,
            request.getRecipientId(),
            request.getOfferedSkillId(),
            request.getRequestedSkillId(),
            request.getMessage(),
            request.getScheduledAt()
        );

        if (exchangeRequestOpt.isPresent()) {
            return ResponseEntity.ok(exchangeRequestOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ExchangeRequest> acceptRequest(@PathVariable String id) {
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestService.updateRequestStatus(id, ExchangeRequest.RequestStatus.ACCEPTED);
        
        if (exchangeRequestOpt.isPresent()) {
            return ResponseEntity.ok(exchangeRequestOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ExchangeRequest> rejectRequest(@PathVariable String id) {
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestService.updateRequestStatus(id, ExchangeRequest.RequestStatus.REJECTED);
        
        if (exchangeRequestOpt.isPresent()) {
            return ResponseEntity.ok(exchangeRequestOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ExchangeRequest> completeRequest(@PathVariable String id) {
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestService.updateRequestStatus(id, ExchangeRequest.RequestStatus.COMPLETED);
        
        if (exchangeRequestOpt.isPresent()) {
            return ResponseEntity.ok(exchangeRequestOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<ExchangeRequestDTO>> getReceivedRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        List<ExchangeRequest> requests = exchangeRequestService.getRequestsForUser(userId);
        List<ExchangeRequestDTO> dtos = exchangeRequestService.convertToDTOList(requests);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<ExchangeRequestDTO>> getSentRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        List<ExchangeRequest> requests = exchangeRequestService.getRequestsFromUser(userId);
        List<ExchangeRequestDTO> dtos = exchangeRequestService.convertToDTOList(requests);
        
        return ResponseEntity.ok(dtos);
    }

    // DTO for request creation
    public static class CreateRequestRequest {
        private String recipientId;
        private String offeredSkillId;
        private String requestedSkillId;
        private String message;
        private LocalDateTime scheduledAt;

        // Getters and Setters
        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getOfferedSkillId() {
            return offeredSkillId;
        }

        public void setOfferedSkillId(String offeredSkillId) {
            this.offeredSkillId = offeredSkillId;
        }

        public String getRequestedSkillId() {
            return requestedSkillId;
        }

        public void setRequestedSkillId(String requestedSkillId) {
            this.requestedSkillId = requestedSkillId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getScheduledAt() {
            return scheduledAt;
        }

        public void setScheduledAt(LocalDateTime scheduledAt) {
            this.scheduledAt = scheduledAt;
        }
    }
}