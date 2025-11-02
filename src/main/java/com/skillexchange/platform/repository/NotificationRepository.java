package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Notification;
import com.skillexchange.platform.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipient(User recipient);
    List<Notification> findByRecipientAndIsReadFalse(User recipient);
}