package com.skillexchange.platform.service;

import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_UserExists() {
        // Arrange
        String userId = "user1";
        User user = new User("testuser", "test@example.com", "password");
        user.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserNotExists() {
        // Arrange
        String userId = "nonexistent";
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());
        
        verify(userRepository, times(1)).findById(userId);
    }
}