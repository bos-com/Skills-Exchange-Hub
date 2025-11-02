package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.NotificationDTO;
import com.skillexchange.platform.entity.Notification;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.NotificationRepository;
import com.skillexchange.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(User recipient, Notification.NotificationType type, 
                                        String title, String message) {
        Notification notification = new Notification(recipient, type, title, message);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(notificationRepository::findByRecipient)
                .orElse(List.of());
    }

    public List<Notification> getUnreadNotificationsForUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(notificationRepository::findByRecipientAndIsReadFalse)
                .orElse(List.of());
    }

    public Notification markAsRead(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return null;
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = getUnreadNotificationsForUser(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setRecipientId(notification.getRecipient().getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public List<NotificationDTO> convertToDTOList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}