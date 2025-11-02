package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.ExchangeRequestDTO;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.entity.Badge;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.SkillRepository;
import com.skillexchange.platform.repository.BadgeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final BadgeRepository badgeRepository;
    private final NotificationService notificationService;

    public ExchangeRequestService(ExchangeRequestRepository exchangeRequestRepository,
                                  UserRepository userRepository,
                                  SkillRepository skillRepository,
                                  BadgeRepository badgeRepository,
                                  NotificationService notificationService) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.badgeRepository = badgeRepository;
        this.notificationService = notificationService;
    }

    public Optional<ExchangeRequest> createRequest(String requesterId, String recipientId, 
                                       String offeredSkillId, String requestedSkillId, 
                                       String message, LocalDateTime scheduledAt) {
        Optional<User> requesterOpt = userRepository.findById(requesterId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);
        Optional<Skill> offeredSkillOpt = skillRepository.findById(offeredSkillId);
        Optional<Skill> requestedSkillOpt = skillRepository.findById(requestedSkillId);

        if (requesterOpt.isPresent() && recipientOpt.isPresent() && 
            offeredSkillOpt.isPresent() && requestedSkillOpt.isPresent()) {
            
            ExchangeRequest request = new ExchangeRequest(
                requesterOpt.get(), 
                recipientOpt.get(), 
                offeredSkillOpt.get(), 
                requestedSkillOpt.get()
            );
            
            request.setMessage(message);
            request.setScheduledAt(scheduledAt);
            
            ExchangeRequest savedRequest = exchangeRequestRepository.save(request);
            
            // Create notification for recipient
            notificationService.createNotification(
                recipientOpt.get(),
                com.skillexchange.platform.entity.Notification.NotificationType.EXCHANGE_REQUEST,
                "New Exchange Request",
                "You have received a new exchange request from " + requesterOpt.get().getUsername()
            );
            
            return Optional.of(savedRequest);
        }
        
        return Optional.empty();
    }

    public Optional<ExchangeRequest> updateRequestStatus(String requestId, ExchangeRequest.RequestStatus status) {
        Optional<ExchangeRequest> requestOpt = exchangeRequestRepository.findById(requestId);
        
        if (requestOpt.isPresent()) {
            ExchangeRequest request = requestOpt.get();
            ExchangeRequest.RequestStatus oldStatus = request.getStatus();
            request.setStatus(status);
            
            ExchangeRequest updatedRequest = exchangeRequestRepository.save(request);
            
            // Create notification for requester
            String notificationTitle = "";
            String notificationMessage = "";
            
            switch (status) {
                case ACCEPTED:
                    notificationTitle = "Exchange Request Accepted";
                    notificationMessage = "Your exchange request has been accepted by " + request.getRecipient().getUsername();
                    break;
                case REJECTED:
                    notificationTitle = "Exchange Request Rejected";
                    notificationMessage = "Your exchange request has been rejected by " + request.getRecipient().getUsername();
                    break;
                case COMPLETED:
                    notificationTitle = "Exchange Completed";
                    notificationMessage = "Your exchange has been marked as completed by " + request.getRecipient().getUsername();
                    
                    // Award badges when exchange is completed
                    awardCompletionBadges(request);
                    break;
                default:
                    break;
            }
            
            if (!notificationTitle.isEmpty()) {
                notificationService.createNotification(
                    request.getRequester(),
                    com.skillexchange.platform.entity.Notification.NotificationType.EXCHANGE_ACCEPTED,
                    notificationTitle,
                    notificationMessage
                );
            }
            
            return Optional.of(updatedRequest);
        }
        
        return Optional.empty();
    }

    public List<ExchangeRequest> getRequestsForUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> exchangeRequestRepository.findByRequester(user))
                .orElse(List.of());
    }

    public List<ExchangeRequest> getRequestsFromUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> exchangeRequestRepository.findByRecipient(user))
                .orElse(List.of());
    }

    public Optional<ExchangeRequest> getRequestById(String requestId) {
        return exchangeRequestRepository.findById(requestId);
    }

    public ExchangeRequestDTO convertToDTO(ExchangeRequest request) {
        ExchangeRequestDTO dto = new ExchangeRequestDTO();
        dto.setId(request.getId());
        dto.setRequesterId(request.getRequester().getId());
        dto.setRequesterName(request.getRequester().getUsername());
        dto.setRecipientId(request.getRecipient().getId());
        dto.setRecipientName(request.getRecipient().getUsername());
        dto.setOfferedSkillId(request.getOfferedSkill().getId());
        dto.setOfferedSkillName(request.getOfferedSkill().getName());
        dto.setRequestedSkillId(request.getRequestedSkill().getId());
        dto.setRequestedSkillName(request.getRequestedSkill().getName());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setScheduledAt(request.getScheduledAt());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }

    public List<ExchangeRequestDTO> convertToDTOList(List<ExchangeRequest> requests) {
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void awardCompletionBadges(ExchangeRequest request) {
        // Award "First Exchange Completed" badge
        awardFirstExchangeBadge(request.getRequester());
        awardFirstExchangeBadge(request.getRecipient());
        
        // Award "Skill Master" badge based on number of completed exchanges
        awardSkillMasterBadge(request.getRequester());
        awardSkillMasterBadge(request.getRecipient());
    }
    
    private void awardFirstExchangeBadge(User user) {
        // Check if user has completed any exchanges before
        List<ExchangeRequest> completedRequests = exchangeRequestRepository.findByStatus(ExchangeRequest.RequestStatus.COMPLETED);
        boolean hasCompletedBefore = completedRequests.stream()
                .anyMatch(req -> req.getRequester().getId().equals(user.getId()) || 
                                req.getRecipient().getId().equals(user.getId()));
        
        // If this is their first completed exchange, award the badge
        if (!hasCompletedBefore) {
            Optional<Badge> firstExchangeBadgeOpt = badgeRepository.findByName("First Exchange Completed");
            if (firstExchangeBadgeOpt.isPresent()) {
                Badge badge = firstExchangeBadgeOpt.get();
                user.getBadges().add(badge);
                userRepository.save(user);
                
                // Create notification
                notificationService.createNotification(
                    user,
                    com.skillexchange.platform.entity.Notification.NotificationType.MESSAGE,
                    "New Badge Earned!",
                    "Congratulations! You've earned the \"First Exchange Completed\" badge."
                );
            }
        }
    }
    
    private void awardSkillMasterBadge(User user) {
        // Count completed exchanges for this user
        List<ExchangeRequest> completedRequests = exchangeRequestRepository.findByStatus(ExchangeRequest.RequestStatus.COMPLETED);
        long userCompletedCount = completedRequests.stream()
                .filter(req -> req.getRequester().getId().equals(user.getId()) || 
                              req.getRecipient().getId().equals(user.getId()))
                .count();
        
        // Award badges based on number of completed exchanges
        if (userCompletedCount >= 10) {
            Optional<Badge> skillMasterBadgeOpt = badgeRepository.findByName("Skill Master");
            if (skillMasterBadgeOpt.isPresent()) {
                Badge badge = skillMasterBadgeOpt.get();
                if (!user.getBadges().contains(badge)) {
                    user.getBadges().add(badge);
                    userRepository.save(user);
                    
                    // Create notification
                    notificationService.createNotification(
                        user,
                        com.skillexchange.platform.entity.Notification.NotificationType.MESSAGE,
                        "New Badge Earned!",
                        "Congratulations! You've earned the \"Skill Master\" badge for completing 10 exchanges."
                    );
                }
            }
        }
    }
}