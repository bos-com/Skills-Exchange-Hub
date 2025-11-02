package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.MessageDTO;
import com.skillexchange.platform.entity.Message;
import com.skillexchange.platform.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDTO sendMessage(@Payload ChatMessage chatMessage) {
        // Save message to database
        Message message = messageService.sendMessage(
            chatMessage.getSenderId(),
            chatMessage.getRecipientId(),
            chatMessage.getContent(),
            chatMessage.getExchangeRequestId()
        );

        if (message != null) {
            // Convert to DTO for sending over WebSocket
            return messageService.convertToDTO(message);
        }

        return null;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
                              SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    // DTO for chat messages
    public static class ChatMessage {
        private String content;
        private String sender;
        private String senderId;
        private String recipientId;
        private String exchangeRequestId;
        private MessageType type;

        public enum MessageType {
            CHAT, JOIN, LEAVE
        }

        // Getters and Setters
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getExchangeRequestId() {
            return exchangeRequestId;
        }

        public void setExchangeRequestId(String exchangeRequestId) {
            this.exchangeRequestId = exchangeRequestId;
        }

        public MessageType getType() {
            return type;
        }

        public void setType(MessageType type) {
            this.type = type;
        }
    }
}