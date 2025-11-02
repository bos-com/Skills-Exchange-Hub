package com.skillexchange.platform;

import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(properties = "spring.data.mongodb.port=0")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // Create a new user
        User user = new User("testuser", "test@example.com", "password123");
        user.setFirstName("Test");
        user.setLastName("User");

        // Save the user
        User savedUser = userRepository.save(user);

        // Verify the user was saved
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        // Find the user by username
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Test");
        assertThat(foundUser.get().getLastName()).isEqualTo("User");
    }

    @Test
    public void testUserExists() {
        // Create a new user
        User user = new User("uniqueuser", "unique@example.com", "password123");
        userRepository.save(user);

        // Check if user exists
        Boolean exists = userRepository.existsByUsername("uniqueuser");
        assertThat(exists).isTrue();

        Boolean emailExists = userRepository.existsByEmail("unique@example.com");
        assertThat(emailExists).isTrue();
    }
}