package com.skillexchange.platform.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void testValidateJwtToken_ValidToken_ReturnsTrue() {
        // Set up the JWT secret for testing
        String jwtSecret = "======================SkillExchange=JWT=Secret===========================";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        
        // Generate a valid token
        String token = generateTestToken(jwtSecret);
        
        // Test validation
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_InvalidToken_ReturnsFalse() {
        // Set up the JWT secret for testing
        String jwtSecret = "======================SkillExchange=JWT=Secret===========================";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        
        // Test with invalid token
        assertFalse(jwtUtils.validateJwtToken("invalid.token.string"));
    }

    @Test
    void testGetUserNameFromJwtToken_ValidToken_ReturnsUsername() {
        // Set up the JWT secret for testing
        String jwtSecret = "======================SkillExchange=JWT=Secret===========================";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        
        // Generate a valid token
        String token = generateTestToken(jwtSecret);
        
        // Test username extraction
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token));
    }

    private String generateTestToken(String jwtSecret) {
        return Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(key(jwtSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key(String jwtSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}