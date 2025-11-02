package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Message;
import com.skillexchange.platform.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySender(User sender);
    List<Message> findByRecipient(User recipient);
    List<Message> findByRecipientAndIsReadFalse(User recipient);
    List<Message> findBySenderAndRecipient(User sender, User recipient);
    List<Message> findByExchangeRequestId(String exchangeRequestId);
}