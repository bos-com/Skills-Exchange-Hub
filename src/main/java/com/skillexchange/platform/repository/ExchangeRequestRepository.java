package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExchangeRequestRepository extends MongoRepository<ExchangeRequest, String> {
    List<ExchangeRequest> findByRequester(User requester);
    List<ExchangeRequest> findByRecipient(User recipient);
    List<ExchangeRequest> findByStatus(RequestStatus status);
}