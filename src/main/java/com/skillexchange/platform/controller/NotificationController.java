package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.NotificationDTO;
import com.skillexchange.platform.entity.Notification;
import com.skillexchange.platform.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        List<NotificationDTO> dtos = notificationService.convertToDTOList(notifications);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        List<NotificationDTO> dtos = notificationService.convertToDTOList(notifications);
        
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        Notification notification = notificationService.markAsRead(id);
        
        if (notification != null) {
            return ResponseEntity.ok(notification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Void> markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok().build();
    }
}