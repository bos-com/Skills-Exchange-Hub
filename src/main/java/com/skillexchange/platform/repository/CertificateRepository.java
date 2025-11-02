package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Certificate;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends MongoRepository<Certificate, String> {
    List<Certificate> findByRecipient(User recipient);
    Optional<Certificate> findByExchangeRequest(ExchangeRequest exchangeRequest);
    boolean existsByExchangeRequest(ExchangeRequest exchangeRequest);
}