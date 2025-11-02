package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Review;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByReviewer(User reviewer);
    List<Review> findByReviewed(User reviewed);
    List<Review> findByExchangeRequest(ExchangeRequest exchangeRequest);
    boolean existsByReviewerAndExchangeRequest(User reviewer, ExchangeRequest exchangeRequest);
}