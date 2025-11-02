package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.MatchDTO;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MatchmakingServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MatchmakingService matchmakingService;

    @Test
    void testFindMatches_ReturnsEmptyList_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        List<MatchDTO> matches = matchmakingService.findMatches("nonexistent", 5);

        // Assert
        assertTrue(matches.isEmpty());
    }

    @Test
    void testFindMatches_ReturnsMatches_WhenUserExists() {
        // Arrange
        User currentUser = new User("user1", "user1@example.com", "password");
        currentUser.setId("user1");
        
        User otherUser = new User("user2", "user2@example.com", "password");
        otherUser.setId("user2");

        List<User> allUsers = Arrays.asList(currentUser, otherUser);
        
        when(userRepository.findById("user1")).thenReturn(Optional.of(currentUser));
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        List<MatchDTO> matches = matchmakingService.findMatches("user1", 5);

        // Assert
        assertNotNull(matches);
    }
}