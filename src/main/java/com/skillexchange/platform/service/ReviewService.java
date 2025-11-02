package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.ReviewDTO;
import com.skillexchange.platform.entity.Review;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.repository.ReviewRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    public ReviewService(ReviewRepository reviewRepository,
                        UserRepository userRepository,
                        ExchangeRequestRepository exchangeRequestRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    public Optional<Review> createReview(String reviewerId, String reviewedId, String exchangeRequestId, int rating, String comment) {
        Optional<User> reviewerOpt = userRepository.findById(reviewerId);
        Optional<User> reviewedOpt = userRepository.findById(reviewedId);
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestRepository.findById(exchangeRequestId);

        if (reviewerOpt.isPresent() && reviewedOpt.isPresent() && exchangeRequestOpt.isPresent()) {
            User reviewer = reviewerOpt.get();
            User reviewed = reviewedOpt.get();
            ExchangeRequest exchangeRequest = exchangeRequestOpt.get();

            // Check if the exchange is completed
            if (exchangeRequest.getStatus() != ExchangeRequest.RequestStatus.COMPLETED) {
                throw new IllegalStateException("Cannot review an exchange that is not completed");
            }

            // Check if reviewer is part of the exchange
            if (!reviewer.getId().equals(exchangeRequest.getRequester().getId()) && 
                !reviewer.getId().equals(exchangeRequest.getRecipient().getId())) {
                throw new IllegalStateException("Reviewer must be part of the exchange");
            }

            // Check if reviewed is part of the exchange
            if (!reviewed.getId().equals(exchangeRequest.getRequester().getId()) && 
                !reviewed.getId().equals(exchangeRequest.getRecipient().getId())) {
                throw new IllegalStateException("Reviewed user must be part of the exchange");
            }

            // Check if reviewer is not the same as reviewed
            if (reviewer.getId().equals(reviewed.getId())) {
                throw new IllegalStateException("Reviewer cannot review themselves");
            }

            // Check if review already exists
            if (reviewRepository.existsByReviewerAndExchangeRequest(reviewer, exchangeRequest)) {
                throw new IllegalStateException("Review already exists for this exchange by this reviewer");
            }

            // Validate rating
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            Review review = new Review(reviewer, reviewed, exchangeRequest, rating, comment);
            return Optional.of(reviewRepository.save(review));
        }

        return Optional.empty();
    }

    public List<Review> getReviewsByUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(reviewRepository::findByReviewed).orElse(List.of());
    }

    public List<Review> getReviewsByReviewer(String reviewerId) {
        Optional<User> reviewerOpt = userRepository.findById(reviewerId);
        return reviewerOpt.map(reviewRepository::findByReviewer).orElse(List.of());
    }

    public List<Review> getReviewsByExchangeRequest(String exchangeRequestId) {
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestRepository.findById(exchangeRequestId);
        return exchangeRequestOpt.map(reviewRepository::findByExchangeRequest).orElse(List.of());
    }

    public double getAverageRatingForUser(String userId) {
        List<Review> reviews = getReviewsByUser(userId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    public ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setReviewerId(review.getReviewer().getId());
        dto.setReviewerName(review.getReviewer().getUsername());
        dto.setReviewedId(review.getReviewed().getId());
        dto.setReviewedName(review.getReviewed().getUsername());
        dto.setExchangeRequestId(review.getExchangeRequest().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public List<ReviewDTO> convertToDTOList(List<Review> reviews) {
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}