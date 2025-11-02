package com.skillexchange.platform.service;

import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.entity.Badge;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.SkillRepository;
import com.skillexchange.platform.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.HashSet;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ExchangeRequestServiceTest {

    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExchangeRequestService exchangeRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateRequestStatus_Completed_AwardsBadges() {
        // Arrange
        String requestId = "request1";
        
        User requester = new User("requester", "requester@example.com", "password");
        requester.setId("requester1");
        requester.setBadges(new HashSet<>());
        
        User recipient = new User("recipient", "recipient@example.com", "password");
        recipient.setId("recipient1");
        recipient.setBadges(new HashSet<>());
        
        Skill offeredSkill = new Skill("Java");
        offeredSkill.setId("skill1");
        
        Skill requestedSkill = new Skill("Python");
        requestedSkill.setId("skill2");
        
        ExchangeRequest exchangeRequest = new ExchangeRequest(requester, recipient, offeredSkill, requestedSkill);
        exchangeRequest.setId(requestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.ACCEPTED);
        
        Badge firstExchangeBadge = new Badge("First Exchange Completed");
        firstExchangeBadge.setId("badge1");
        
        when(exchangeRequestRepository.findById(requestId)).thenReturn(Optional.of(exchangeRequest));
        when(exchangeRequestRepository.save(exchangeRequest)).thenReturn(exchangeRequest);
        when(badgeRepository.findByName("First Exchange Completed")).thenReturn(Optional.of(firstExchangeBadge));
        when(exchangeRequestRepository.findByStatus(ExchangeRequest.RequestStatus.COMPLETED)).thenReturn(new ArrayList<>());
        
        // Act
        Optional<ExchangeRequest> result = exchangeRequestService.updateRequestStatus(requestId, ExchangeRequest.RequestStatus.COMPLETED);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(ExchangeRequest.RequestStatus.COMPLETED, result.get().getStatus());
        
        // Verify that badges were awarded
        verify(userRepository, times(2)).save(any(User.class)); // Once for requester, once for recipient
        verify(notificationService, times(2)).createNotification(
            any(User.class),
            any(),
            anyString(),
            anyString()
        );
    }
}