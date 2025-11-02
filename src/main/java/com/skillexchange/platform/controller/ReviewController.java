package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.ReviewDTO;
import com.skillexchange.platform.entity.Review;
import com.skillexchange.platform.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Review> createReview(@RequestBody CreateReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String reviewerId = authentication.getName(); // Assuming username is the user ID

        try {
            Optional<Review> reviewOpt = reviewService.createReview(
                reviewerId,
                request.getReviewedId(),
                request.getExchangeRequestId(),
                request.getRating(),
                request.getComment()
            );

            if (reviewOpt.isPresent()) {
                return ResponseEntity.ok(reviewOpt.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("Error", e.getMessage()).build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsForUser(@PathVariable String userId) {
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        List<ReviewDTO> dtos = reviewService.convertToDTOList(reviews);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsByReviewer(@PathVariable String reviewerId) {
        List<Review> reviews = reviewService.getReviewsByReviewer(reviewerId);
        List<ReviewDTO> dtos = reviewService.convertToDTOList(reviews);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/exchange/{exchangeRequestId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsForExchange(@PathVariable String exchangeRequestId) {
        List<Review> reviews = reviewService.getReviewsByExchangeRequest(exchangeRequestId);
        List<ReviewDTO> dtos = reviewService.convertToDTOList(reviews);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}/average")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Double> getAverageRatingForUser(@PathVariable String userId) {
        double averageRating = reviewService.getAverageRatingForUser(userId);
        return ResponseEntity.ok(averageRating);
    }

    // DTO for review creation
    public static class CreateReviewRequest {
        private String reviewedId;
        private String exchangeRequestId;
        private int rating;
        private String comment;

        // Getters and Setters
        public String getReviewedId() {
            return reviewedId;
        }

        public void setReviewedId(String reviewedId) {
            this.reviewedId = reviewedId;
        }

        public String getExchangeRequestId() {
            return exchangeRequestId;
        }

        public void setExchangeRequestId(String exchangeRequestId) {
            this.exchangeRequestId = exchangeRequestId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}