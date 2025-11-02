package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.MessageDTO;
import com.skillexchange.platform.entity.Message;
import com.skillexchange.platform.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Message> sendMessage(@RequestBody SendMessageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderId = authentication.getName(); // Assuming username is the user ID

        Message message = messageService.sendMessage(
            senderId,
            request.getRecipientId(),
            request.getContent(),
            request.getExchangeRequestId()
        );

        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conversation/{userId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // Assuming username is the user ID

        List<Message> messages = messageService.getMessagesBetweenUsers(currentUserId, userId);
        List<MessageDTO> dtos = messageService.convertToDTOList(messages);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/exchange/{exchangeRequestId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<MessageDTO>> getMessagesForExchange(@PathVariable String exchangeRequestId) {
        List<Message> messages = messageService.getMessagesForExchange(exchangeRequestId);
        List<MessageDTO> dtos = messageService.convertToDTOList(messages);
        
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Message> markAsRead(@PathVariable String id) {
        Message message = messageService.markAsRead(id);
        
        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO for sending messages
    public static class SendMessageRequest {
        private String recipientId;
        private String content;
        private String exchangeRequestId;

        // Getters and Setters
        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExchangeRequestId() {
            return exchangeRequestId;
        }

        public void setExchangeRequestId(String exchangeRequestId) {
            this.exchangeRequestId = exchangeRequestId;
        }
    }
}