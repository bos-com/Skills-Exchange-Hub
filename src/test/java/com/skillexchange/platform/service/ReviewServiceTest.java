package com.skillexchange.platform.service;

import com.skillexchange.platform.entity.Review;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.repository.ReviewRepository;
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

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview_Success() {
        // Arrange
        String reviewerId = "reviewer1";
        String reviewedId = "reviewed1";
        String exchangeRequestId = "exchange1";
        int rating = 5;
        String comment = "Great exchange!";

        User reviewer = new User("reviewer", "reviewer@example.com", "password");
        reviewer.setId(reviewerId);

        User reviewed = new User("reviewed", "reviewed@example.com", "password");
        reviewed.setId(reviewedId);

        Skill offeredSkill = new Skill("Java");
        Skill requestedSkill = new Skill("Python");

        ExchangeRequest exchangeRequest = new ExchangeRequest(reviewer, reviewed, offeredSkill, requestedSkill);
        exchangeRequest.setId(exchangeRequestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.COMPLETED);
        exchangeRequest.setCompletedAt(LocalDateTime.now());

        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(userRepository.findById(reviewedId)).thenReturn(Optional.of(reviewed));
        when(exchangeRequestRepository.findById(exchangeRequestId)).thenReturn(Optional.of(exchangeRequest));
        when(reviewRepository.existsByReviewerAndExchangeRequest(reviewer, exchangeRequest)).thenReturn(false);

        Review savedReview = new Review(reviewer, reviewed, exchangeRequest, rating, comment);
        savedReview.setId("review1");
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act
        Optional<Review> result = reviewService.createReview(reviewerId, reviewedId, exchangeRequestId, rating, comment);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(reviewerId, result.get().getReviewer().getId());
        assertEquals(reviewedId, result.get().getReviewed().getId());
        assertEquals(exchangeRequestId, result.get().getExchangeRequest().getId());
        assertEquals(rating, result.get().getRating());
        assertEquals(comment, result.get().getComment());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReview_ExchangeNotCompleted() {
        // Arrange
        String reviewerId = "reviewer1";
        String reviewedId = "reviewed1";
        String exchangeRequestId = "exchange1";
        int rating = 5;
        String comment = "Great exchange!";

        User reviewer = new User("reviewer", "reviewer@example.com", "password");
        reviewer.setId(reviewerId);

        User reviewed = new User("reviewed", "reviewed@example.com", "password");
        reviewed.setId(reviewedId);

        Skill offeredSkill = new Skill("Java");
        Skill requestedSkill = new Skill("Python");

        ExchangeRequest exchangeRequest = new ExchangeRequest(reviewer, reviewed, offeredSkill, requestedSkill);
        exchangeRequest.setId(exchangeRequestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.ACCEPTED); // Not completed

        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(userRepository.findById(reviewedId)).thenReturn(Optional.of(reviewed));
        when(exchangeRequestRepository.findById(exchangeRequestId)).thenReturn(Optional.of(exchangeRequest));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(reviewerId, reviewedId, exchangeRequestId, rating, comment);
        });

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReview_InvalidRating() {
        // Arrange
        String reviewerId = "reviewer1";
        String reviewedId = "reviewed1";
        String exchangeRequestId = "exchange1";
        int rating = 0; // Invalid rating
        String comment = "Great exchange!";

        User reviewer = new User("reviewer", "reviewer@example.com", "password");
        reviewer.setId(reviewerId);

        User reviewed = new User("reviewed", "reviewed@example.com", "password");
        reviewed.setId(reviewedId);

        Skill offeredSkill = new Skill("Java");
        Skill requestedSkill = new Skill("Python");

        ExchangeRequest exchangeRequest = new ExchangeRequest(reviewer, reviewed, offeredSkill, requestedSkill);
        exchangeRequest.setId(exchangeRequestId);
        exchangeRequest.setStatus(ExchangeRequest.RequestStatus.COMPLETED);
        exchangeRequest.setCompletedAt(LocalDateTime.now());

        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(userRepository.findById(reviewedId)).thenReturn(Optional.of(reviewed));
        when(exchangeRequestRepository.findById(exchangeRequestId)).thenReturn(Optional.of(exchangeRequest));
        when(reviewRepository.existsByReviewerAndExchangeRequest(reviewer, exchangeRequest)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(reviewerId, reviewedId, exchangeRequestId, rating, comment);
        });

        verify(reviewRepository, never()).save(any(Review.class));
    }
}