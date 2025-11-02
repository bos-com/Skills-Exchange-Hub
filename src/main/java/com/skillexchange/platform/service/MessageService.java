package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.MessageDTO;
import com.skillexchange.platform.entity.Message;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.MessageRepository;
import com.skillexchange.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(String senderId, String recipientId, String content, String exchangeRequestId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            Message message = new Message(senderOpt.get(), recipientOpt.get(), content);
            message.setExchangeRequestId(exchangeRequestId);
            Message savedMessage = messageRepository.save(message);
            
            return savedMessage;
        }
        
        return null;
    }

    public List<Message> getMessagesBetweenUsers(String userId1, String userId2) {
        Optional<User> user1Opt = userRepository.findById(userId1);
        Optional<User> user2Opt = userRepository.findById(userId2);
        
        if (user1Opt.isPresent() && user2Opt.isPresent()) {
            User user1 = user1Opt.get();
            User user2 = user2Opt.get();
            
            // Get messages where user1 is sender and user2 is recipient
            List<Message> messages1 = messageRepository.findBySenderAndRecipient(user1, user2);
            
            // Get messages where user2 is sender and user1 is recipient
            List<Message> messages2 = messageRepository.findBySenderAndRecipient(user2, user1);
            
            // Combine and sort by creation time
            messages1.addAll(messages2);
            messages1.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
            
            return messages1;
        }
        
        return List.of();
    }

    public List<Message> getMessagesForExchange(String exchangeRequestId) {
        return messageRepository.findByExchangeRequestId(exchangeRequestId);
    }

    public Message markAsRead(String messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setRead(true);
            return messageRepository.save(message);
        }
        return null;
    }

    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setRecipientId(message.getRecipient().getId());
        dto.setRecipientName(message.getRecipient().getUsername());
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setExchangeRequestId(message.getExchangeRequestId());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

    public List<MessageDTO> convertToDTOList(List<Message> messages) {
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}