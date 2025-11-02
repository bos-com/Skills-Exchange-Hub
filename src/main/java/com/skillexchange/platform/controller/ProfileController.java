package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.UserProfileDTO;
import com.skillexchange.platform.dto.MatchDTO;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.service.UserService;
import com.skillexchange.platform.service.MatchmakingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final MatchmakingService matchmakingService;

    public ProfileController(UserService userService, MatchmakingService matchmakingService) {
        this.userService = userService;
        this.matchmakingService = matchmakingService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<User> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID
        
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody UserProfileDTO profileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID
        
        return userService.updateUserProfile(userId, profileDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/matches")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<MatchDTO>> getMatches(@RequestParam(defaultValue = "10") int limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID
        
        List<MatchDTO> matches = matchmakingService.findMatches(userId, limit);
        return ResponseEntity.ok(matches);
    }
}