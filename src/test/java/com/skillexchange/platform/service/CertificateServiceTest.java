package com.skillexchange.platform.service;

import com.skillexchange.platform.entity.Certificate;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.repository.CertificateRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @InjectMocks
    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCertificate_Success() throws Exception {
        // Arrange
        String exchangeRequestId = "exchange1";
        
        User requester = new User("requester", "requester@example.com", "password");
        requester.setId("requester1");
        
        User recipient = new User("recipient", "recipient@example.com", "password");
        recipient.setId("recipient1");
        
        Skill offeredSkill = new Skill("Java");
        offeredSkill.setId("skill1");
        
        Skill requestedSkill = new Skill("Python");
        requestedSkill.setId("skill2");
        
        ExchangeRequest exchangeRequest = new ExchangeRequest(requester, recipient, offeredSkill, requestedSkill);
        exchangeRequest.setId(exchangeRequestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.COMPLETED);
        exchangeRequest.setCompletedAt(LocalDateTime.now());
        
        Certificate certificate = new Certificate(requester, exchangeRequest, "Completion Certificate", "Description");
        certificate.setId("certificate1");
        
        when(exchangeRequestRepository.findById(exchangeRequestId)).thenReturn(Optional.of(exchangeRequest));
        when(certificateRepository.existsByExchangeRequest(exchangeRequest)).thenReturn(false);
        when(certificateRepository.findByExchangeRequest(exchangeRequest)).thenReturn(Optional.of(certificate));
        
        // Act
        Optional<Certificate> result = certificateService.generateCertificate(exchangeRequestId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("certificate1", result.get().getId());
        assertEquals(requester.getId(), result.get().getRecipient().getId());
        assertEquals(exchangeRequestId, result.get().getExchangeRequest().getId());
        
        verify(certificateRepository, times(2)).save(any(Certificate.class)); // Once for requester, once for recipient
    }

    @Test
    void testGenerateCertificate_ExchangeNotCompleted() throws Exception {
        // Arrange
        String exchangeRequestId = "exchange1";
        
        User requester = new User("requester", "requester@example.com", "password");
        requester.setId("requester1");
        
        User recipient = new User("recipient", "recipient@example.com", "password");
        recipient.setId("recipient1");
        
        Skill offeredSkill = new Skill("Java");
        offeredSkill.setId("skill1");
        
        Skill requestedSkill = new Skill("Python");
        requestedSkill.setId("skill2");
        
        ExchangeRequest exchangeRequest = new ExchangeRequest(requester, recipient, offeredSkill, requestedSkill);
        exchangeRequest.setId(exchangeRequestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.ACCEPTED); // Not completed
        
        when(exchangeRequestRepository.findById(exchangeRequestId)).thenReturn(Optional.of(exchangeRequest));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            certificateService.generateCertificate(exchangeRequestId);
        });
        
        verify(certificateRepository, never()).save(any(Certificate.class));
    }
}